package edu.umn.cs.recsys.ii;

import org.grouplens.lenskit.basic.AbstractItemScorer;
import org.grouplens.lenskit.data.dao.UserEventDAO;
import org.grouplens.lenskit.data.event.Rating;
import org.grouplens.lenskit.data.history.History;
import org.grouplens.lenskit.data.history.RatingVectorUserHistorySummarizer;
import org.grouplens.lenskit.data.history.UserHistory;
import org.grouplens.lenskit.knn.NeighborhoodSize;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.SparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class SimpleItemItemScorer extends AbstractItemScorer {
    private final SimpleItemItemModel model;
    private final UserEventDAO userEvents;
    private final int neighborhoodSize;

    @Inject
    public SimpleItemItemScorer(SimpleItemItemModel m, UserEventDAO dao,
                                @NeighborhoodSize int nnbrs) {
        model = m;
        userEvents = dao;
        neighborhoodSize = nnbrs;
    }

    /**
     * Score items for a user.
     * @param user The user ID.
     * @param scores The score vector.  Its key domain is the items to score, and the scores
     *               (rating predictions) should be written back to this vector.
     */
    @Override
    public void score(long user, @Nonnull MutableSparseVector scores) {
        SparseVector ratings = getUserRatingVector(user);

        for (VectorEntry e: scores.fast(VectorEntry.State.EITHER)) {
            long item = e.getKey();
            List<ScoredId> neighbors = model.getNeighbors(item);
            PriorityQueue<ScoredId> valid = new PriorityQueue<ScoredId>(neighborhoodSize, new Comparator<ScoredId>() {
                public int compare(ScoredId n1, ScoredId n2) {
                    if(n1.getScore() > n2.getScore())
                    	return -1;
                    else if (n1.getScore() < n2.getScore())
                    	return 1;
                    else
                    	return 0;
                }
            });
            Set<Long> ratingIds = ratings.keySet();
            
            
            for(ScoredId neighbor : neighbors){
            	if(ratingIds.contains(neighbor.getId()))
            	{
            		valid.add(neighbor);
            	}
            }
            Double nominator = 0d;
            Double denominator = 0d;
            Boolean end = false;
            
            for(int i=1; i <= neighborhoodSize; i++)
            {
            	ScoredId value = valid.poll();
            	if(value == null)
            	{
            		break;
            	}
            	else
            	{
            		denominator += value.getScore();
            		nominator = (value.getScore() * ratings.get(value.getId())) + nominator ;
            	}
            	
            }
            
            if (!end) scores.set(e.getKey(), nominator/denominator);
            
        }
    }

    /**
     * Get a user's ratings.
     * @param user The user ID.
     * @return The ratings to retrieve.
     */
    private SparseVector getUserRatingVector(long user) {
        UserHistory<Rating> history = userEvents.getEventsForUser(user, Rating.class);
        if (history == null) {
            history = History.forUser(user);
        }

        return RatingVectorUserHistorySummarizer.makeRatingVector(history);
    }
}
