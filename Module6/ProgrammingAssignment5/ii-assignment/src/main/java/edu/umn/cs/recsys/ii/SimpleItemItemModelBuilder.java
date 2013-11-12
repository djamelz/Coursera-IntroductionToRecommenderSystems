package edu.umn.cs.recsys.ii;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import org.grouplens.lenskit.collections.LongUtils;
import org.grouplens.lenskit.core.Transient;
import org.grouplens.lenskit.cursors.Cursor;
import org.grouplens.lenskit.data.dao.ItemDAO;
import org.grouplens.lenskit.data.dao.UserEventDAO;
import org.grouplens.lenskit.data.event.Event;
import org.grouplens.lenskit.data.history.RatingVectorUserHistorySummarizer;
import org.grouplens.lenskit.data.history.UserHistory;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.scored.ScoredIdBuilder;
import org.grouplens.lenskit.scored.ScoredIdListBuilder;
import org.grouplens.lenskit.scored.ScoredIds;
import org.grouplens.lenskit.vectors.ImmutableSparseVector;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.SparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class SimpleItemItemModelBuilder implements Provider<SimpleItemItemModel> {
    private final ItemDAO itemDao;
    private final UserEventDAO userEventDao;
    private static final Logger logger = LoggerFactory.getLogger(SimpleItemItemModelBuilder.class);;

    @Inject
    public SimpleItemItemModelBuilder(@Transient ItemDAO idao,
                                      @Transient UserEventDAO uedao) {
        itemDao = idao;
        userEventDao = uedao;
    }

    @Override
    public SimpleItemItemModel get() {
        // Get the transposed rating matrix
        // This gives us a map of item IDs to those items' rating vectors
        Map<Long, ImmutableSparseVector> itemVectors = getItemVectors();

        // Get all items - you might find this useful
        Set<Long> items = new HashSet<Long>(LongUtils.packedSet(itemVectors.keySet()));
        
        Map<Long, ScoredIdListBuilder> simBuilder = new HashMap<Long, ScoredIdListBuilder>();
        Map<Long, List<ScoredId>> sim = new HashMap<Long, List<ScoredId>>();
        for(Long item : items){
        	simBuilder.put(item, new ScoredIdListBuilder());
        }
        
        for(Entry<Long, ImmutableSparseVector> itemVector : itemVectors.entrySet()){
        	items.remove(itemVector.getKey());
        	for(Long item : items)
        	{        	
        		Double cos = computeCosine(itemVector.getValue(), itemVectors.get(item));
        		if(cos > 0)
        		{
        			simBuilder.get(itemVector.getKey()).add(item, cos);
        			simBuilder.get(item).add(itemVector.getKey(), cos);
        		}
        	}
        	sim.put(itemVector.getKey(), simBuilder.get(itemVector.getKey()).finish());
        }
        

        // TODO Compute the similarities between each pair of items
        // It will need to be in a map of longs to lists of Scored IDs to store in the model
        
        return new SimpleItemItemModel(sim);
    }
    
    private Double computeCosine(ImmutableSparseVector v1, ImmutableSparseVector v2){
    	Set<Long> intersect = new HashSet<Long>(v1.keySet());

    	intersect.retainAll(v2.keySet());
    	
    	if(intersect.size() == 0) return 0d;
    	
    	MutableSparseVector nv1 = MutableSparseVector.create(intersect);
    	MutableSparseVector nv2 = MutableSparseVector.create(intersect);
    	
    	for(Long i : intersect)
    	{
    		nv1.set(i, v1.get(i));
    		nv2.set(i, v2.get(i));
    	}
    	
    	return nv1.dot(nv2)/(v1.norm()*v2.norm());
    }

    /**
     * Load the data into memory, indexed by item.
     * @return A map from item IDs to item rating vectors. Each vector contains users' ratings for
     * the item, keyed by user ID.
     */
    public Map<Long,ImmutableSparseVector> getItemVectors() {
        // set up storage for building each item's rating vector
        LongSet items = itemDao.getItemIds();
        // map items to maps from users to ratings
        Map<Long,Map<Long,Double>> itemData = new HashMap<Long, Map<Long, Double>>();
        for (long item: items) {
            itemData.put(item, new HashMap<Long, Double>());
        }
        // itemData should now contain a map to accumulate the ratings of each item

        // stream over all user events
        Cursor<UserHistory<Event>> stream = userEventDao.streamEventsByUser();
        try {
            for (UserHistory<Event> evt: stream) {
                MutableSparseVector vector = RatingVectorUserHistorySummarizer.makeRatingVector(evt).mutableCopy();
                Double mean = vector.mean();
                
                // vector is now the user's rating vector
                // TODO Normalize this vector and store the ratings in the item data
                for(Long item : vector.keySet())
                {
                	Map<Long,Double> t = itemData.get(item);
                	t.put(evt.getUserId(), vector.get(item) - mean);
                	itemData.put(item, t);
                }
            }
        } finally {
            stream.close();
        }

        // This loop converts our temporary item storage to a map of item vectors
        Map<Long,ImmutableSparseVector> itemVectors = new HashMap<Long, ImmutableSparseVector>();
        for (Map.Entry<Long,Map<Long,Double>> entry: itemData.entrySet()) {
            MutableSparseVector vec = MutableSparseVector.create(entry.getValue());
            itemVectors.put(entry.getKey(), vec.immutable());
        }
        return itemVectors;
    }
}
