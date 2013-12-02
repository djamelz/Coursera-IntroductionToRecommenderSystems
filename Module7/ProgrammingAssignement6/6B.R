doc <- read.csv("/projects/Coursera-IntroductionToRecommenderSystems/Module7/ProgrammingAssignement6/eval-results.csv")

#Q1
rmseU <- sapply(unique(doc$Algorithm), function(x) mean(doc$RMSE.ByUser[(doc$Algorithm == x)], na.rm=TRUE))
ndcg <- sapply(unique(doc$Algorithm), function(x) mean(doc$nDCG[(doc$Algorithm == x)], na.rm=TRUE))
names(rmseU) <- unique(doc$Algorithm)
names(ndcg) <- unique(doc$Algorithm)

#Q2
q2rmseU <- sapply(unique(doc$Algorithm), function(x) mean(doc$RMSE.ByUser[(doc$Algorithm == x) & (doc$DataSet == "FullData")], na.rm=TRUE))
q2ndcg <- sapply(unique(doc$Algorithm), function(x) mean(doc$nDCG[(doc$Algorithm == x) & (doc$DataSet == "FullData")], na.rm=TRUE))
q2rmseR <- sapply(unique(doc$Algorithm), function(x) mean(doc$RMSE.ByRating[(doc$Algorithm == x) & (doc$DataSet == "FullData")], na.rm=TRUE))
q2topNdcg <- sapply(unique(doc$Algorithm), function(x) mean(doc$TopN.nDCG[(doc$Algorithm == x)& (doc$DataSet == "FullData")], na.rm=TRUE))
names(q2rmseU) <- unique(doc$Algorithm)
names(q2ndcg) <- unique(doc$Algorithm)
names(q2rmseR) <- unique(doc$Algorithm)
names(q2topNdcg) <- unique(doc$Algorithm)

#Q3
q3rmseU <- sapply(unique(doc$FeatureCount), function(x) mean(doc$RMSE.ByUser[(doc$FeatureCount == x)], na.rm=TRUE))
q3ndcg <- sapply(unique(doc$FeatureCount), function(x) mean(doc$nDCG[(doc$FeatureCount == x)], na.rm=TRUE))
q3rmseR <- sapply(unique(doc$FeatureCount), function(x) mean(doc$RMSE.ByRating[(doc$FeatureCount == x)], na.rm=TRUE))
q3topNdcg <- sapply(unique(doc$FeatureCount), function(x) mean(doc$TopN.nDCG[(doc$FeatureCount == x)], na.rm=TRUE))
names(q3rmseR) <- unique(doc$FeatureCount)
names(q3rmseU) <- unique(doc$FeatureCount)
names(q3ndcg) <- unique(doc$FeatureCount)
names(q3topNdcg) <- unique(doc$FeatureCount)