doc <- read.csv("/projects/Coursera-IntroductionToRecommenderSystems/Module6/ProgrammingAssignment5/eval-results.csv")

#Q1
q1 <- sapply(unique(doc$NNbrs), function(x) mean(doc$RMSE.ByUser[(doc$NNbrs == x) & (doc$Algorithm == "CustomItemItem")], na.rm=TRUE))
names(q1) <- unique(doc$NNbrs)

#Q2
dataset <- unique(doc$DataSet)
q2a <- sapply(dataset, function(x) mean(doc$RMSE.ByUser[(doc$DataSet == x) & (doc$Algorithm == "CustomItemItem")], na.rm=TRUE))
q2b <- sapply(dataset, function(x) mean(doc$nDCG[(doc$DataSet == x) & (doc$Algorithm == "CustomItemItem")], na.rm=TRUE))
names(q2a) <- dataset
names(q2b) <- dataset

