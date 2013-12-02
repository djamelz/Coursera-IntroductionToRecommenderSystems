doc <-read.csv("/Users/Djamel/Downloads/eval-results2.csv",sep=";")
doc2 <-read.csv("/Users/Djamel/Downloads/eval-results.csv")
algo = doc$Algorithm[doc$Partition == 0]
algo2 = doc2$Algorithm[doc2$Partition == 0]
topDCGmeans <- sapply(algo, function(x) mean(doc$TopN.nDCG[doc$Algorithm == x], na.rm=TRUE))
sapply(doc[11:ncol(doc)], function(x) mean(x[doc$Algorithm == "UserUserNorm100"], na.rm=TRUE))
names(topDCGmeans) = algo


