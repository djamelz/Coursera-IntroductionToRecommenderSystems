doc <- doc <-read.csv(file='/projects/Coursera-IntroductionToRecommenderSystems/Module2/WrittenAssignment2/recsys-data-WA 1 Rating Matrix.csv')

#Part1
sort(sapply(doc[,2:21], function(x) round(mean(x,na.rm=TRUE), 2)),decreasing=TRUE)[1:5]


#Part2
sort(sapply(doc[,2:21], function(x) round(sum(x>=4,na.rm=TRUE) / sum(complete.cases(x)) * 100, 2)),decreasing=TRUE)[1:5]

#Part3
sort(sapply(doc[,2:21], function(x) sum(complete.cases(x))),decreasing=TRUE)[1:5]


#Part4
sort(sapply(doc[,3:21], function(x) sum(complete.cases(doc$X260..Star.Wars..Episode.IV...A.New.Hope..1977.) & complete.cases(x))/sum(complete.cases(doc$X260..Star.Wars..Episode.IV...A.New.Hope..1977.))),decreasing=TRUE)[1:5]
