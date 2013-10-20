
sim <- function(a){
  sapply(doc[,-(1)], corNA, a = a)
}

corNA<- function (a, i){
  na = complete.cases(a & i)
  cor(a[na], i[na], method=c("pearson"))
}

part2calc <- function(user, doc){
  #doc <-read.csv(file='/projects/Coursera-IntroductionToRecommenderSystems/ProgrammingAssignment/src/main/resources/module4/recsys-data-sample-rating-matrix.csv')
  topsim = sort(sim(user), decreasing=TRUE)[2:6]
  topsim
  #sort(sim(doc$X3712), decreasing=TRUE)[2:6]
  weightedRatings = as.data.frame(sapply(names(topsim), function(x) (doc[x] - mean(t(doc[x]), na.rm=TRUE)) * topsim[x]))
  weightedRatings
  ret = mean(user, na.rm=TRUE) + as.vector(sapply(1:nrow(weightedRatings), function(x) sum(weightedRatings[x,], na.rm=TRUE) / sum(topsim[complete.cases(t(weightedRatings[x,]))])))
  names(ret) <- doc$X
  sapply(sort(ret, decreasing=TRUE)[1:3], function(x) round(x, 3))
}

part2 <- function(){
  doc <-read.csv(file='/projects/Coursera-IntroductionToRecommenderSystems/ProgrammingAssignment/src/main/resources/module4/recsys-data-sample-rating-matrix.csv')
  print("User 3712 :")
  print(part2calc(doc$X3712, doc))
  print("User 3867 :")
  print(part2calc(doc$X3867, doc))
  print("User 860 :")
  print(part2calc(doc$X860, doc))
  
}
