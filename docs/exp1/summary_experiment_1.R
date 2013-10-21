#
# Creates a summary of a experiment trial
# Generates plots to describe the trial data
#

#
# Experiment 1, trial 1
#
experiment_data <- agg(experiment_name="e1t1", number_of_runs=1);
experiment_name <- "e1t1"
sub_title <- paste(", Experiment:", experiment_name)
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
#factories <- read.csv(file="factories-trial1.csv")
#p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
  scale_x_discrete()

ggsave(filename=paste("plots/", experiment_name, "-max_min_delta.pdf", sep=''), plot=p1)
ggsave(filename=paste("plots/", experiment_name, "-histogram_delta.pdf", sep=''), plot=p2)
ggsave(filename=paste("plots/", experiment_name, "-points_delta.pdf", sep=''), plot=p3)
ggsave(filename=paste("plots/", experiment_name, "-histogram_services_engaged.pdf", sep=''), plot=p4)
ggsave(filename=paste("plots/", experiment_name, "-histogram_avg_execution_times_factories.pdf", sep=''), plot=p5)


experiment_data <- agg(experiment_name="e1t10", number_of_runs=1);
experiment_name <- "e1t10"
sub_title <- paste(", Experiment:", experiment_name)
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
#factories <- read.csv(file="factories-trial1.csv")
#p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
scale_x_discrete()

ggsave(filename=paste("plots/", experiment_name, "-max_min_delta.pdf", sep=''), plot=p1)
ggsave(filename=paste("plots/", experiment_name, "-histogram_delta.pdf", sep=''), plot=p2)
ggsave(filename=paste("plots/", experiment_name, "-points_delta.pdf", sep=''), plot=p3)
ggsave(filename=paste("plots/", experiment_name, "-histogram_services_engaged.pdf", sep=''), plot=p4)
ggsave(filename=paste("plots/", experiment_name, "-histogram_avg_execution_times_factories.pdf", sep=''), plot=p5)

experiment_data <- agg(experiment_name="e1t9", number_of_runs=1);
experiment_name <- "e1t9"
sub_title <- paste(", Experiment:", experiment_name)
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
#factories <- read.csv(file="factories-trial1.csv")
#p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
scale_x_discrete()

ggsave(filename=paste("plots/", experiment_name, "-max_min_delta.pdf", sep=''), plot=p1)
ggsave(filename=paste("plots/", experiment_name, "-histogram_delta.pdf", sep=''), plot=p2)
ggsave(filename=paste("plots/", experiment_name, "-points_delta.pdf", sep=''), plot=p3)
ggsave(filename=paste("plots/", experiment_name, "-histogram_services_engaged.pdf", sep=''), plot=p4)
ggsave(filename=paste("plots/", experiment_name, "-histogram_avg_execution_times_factories.pdf", sep=''), plot=p5)


experiment_data <- agg(experiment_name="e1t8", number_of_runs=1);
experiment_name <- "e1t8"
sub_title <- paste(", Experiment:", experiment_name)
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
#factories <- read.csv(file="factories-trial1.csv")
#p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
scale_x_discrete()

ggsave(filename=paste("plots/", experiment_name, "-max_min_delta.pdf", sep=''), plot=p1)
ggsave(filename=paste("plots/", experiment_name, "-histogram_delta.pdf", sep=''), plot=p2)
ggsave(filename=paste("plots/", experiment_name, "-points_delta.pdf", sep=''), plot=p3)
ggsave(filename=paste("plots/", experiment_name, "-histogram_services_engaged.pdf", sep=''), plot=p4)
ggsave(filename=paste("plots/", experiment_name, "-histogram_avg_execution_times_factories.pdf", sep=''), plot=p5)

experiment_data <- agg(experiment_name="e1t7", number_of_runs=1);
experiment_name <- "e1t7"
sub_title <- paste(", Experiment:", experiment_name)
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
#factories <- read.csv(file="factories-trial1.csv")
#p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
scale_x_discrete()

ggsave(filename=paste("plots/", experiment_name, "-max_min_delta.pdf", sep=''), plot=p1)
ggsave(filename=paste("plots/", experiment_name, "-histogram_delta.pdf", sep=''), plot=p2)
ggsave(filename=paste("plots/", experiment_name, "-points_delta.pdf", sep=''), plot=p3)
ggsave(filename=paste("plots/", experiment_name, "-histogram_services_engaged.pdf", sep=''), plot=p4)
ggsave(filename=paste("plots/", experiment_name, "-histogram_avg_execution_times_factories.pdf", sep=''), plot=p5)


experiment_data <- agg(experiment_name="e1t6", number_of_runs=1);
experiment_name <- "e1t6"
sub_title <- paste(", Experiment:", experiment_name)
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
#factories <- read.csv(file="factories-trial1.csv")
#p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
scale_x_discrete()

ggsave(filename=paste("plots/", experiment_name, "-max_min_delta.pdf", sep=''), plot=p1)
ggsave(filename=paste("plots/", experiment_name, "-histogram_delta.pdf", sep=''), plot=p2)
ggsave(filename=paste("plots/", experiment_name, "-points_delta.pdf", sep=''), plot=p3)
ggsave(filename=paste("plots/", experiment_name, "-histogram_services_engaged.pdf", sep=''), plot=p4)
ggsave(filename=paste("plots/", experiment_name, "-histogram_avg_execution_times_factories.pdf", sep=''), plot=p5)

experiment_data <- agg(experiment_name="e1t5", number_of_runs=1);
experiment_name <- "e1t5"
sub_title <- paste(", Experiment:", experiment_name)
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
#factories <- read.csv(file="factories-trial1.csv")
#p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
scale_x_discrete()

ggsave(filename=paste("plots/", experiment_name, "-max_min_delta.pdf", sep=''), plot=p1)
ggsave(filename=paste("plots/", experiment_name, "-histogram_delta.pdf", sep=''), plot=p2)
ggsave(filename=paste("plots/", experiment_name, "-points_delta.pdf", sep=''), plot=p3)
ggsave(filename=paste("plots/", experiment_name, "-histogram_services_engaged.pdf", sep=''), plot=p4)
ggsave(filename=paste("plots/", experiment_name, "-histogram_avg_execution_times_factories.pdf", sep=''), plot=p5)



experiment_data <- agg(experiment_name="e1t4", number_of_runs=1);
experiment_name <- "e1t4"
sub_title <- paste(", Experiment:", experiment_name)
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
#factories <- read.csv(file="factories-trial1.csv")
#p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
scale_x_discrete()

ggsave(filename=paste("plots/", experiment_name, "-max_min_delta.pdf", sep=''), plot=p1)
ggsave(filename=paste("plots/", experiment_name, "-histogram_delta.pdf", sep=''), plot=p2)
ggsave(filename=paste("plots/", experiment_name, "-points_delta.pdf", sep=''), plot=p3)
ggsave(filename=paste("plots/", experiment_name, "-histogram_services_engaged.pdf", sep=''), plot=p4)
ggsave(filename=paste("plots/", experiment_name, "-histogram_avg_execution_times_factories.pdf", sep=''), plot=p5)


experiment_data <- agg(experiment_name="e1t3", number_of_runs=1);
experiment_name <- "e1t3"
sub_title <- paste(", Experiment:", experiment_name)
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
#factories <- read.csv(file="factories-trial1.csv")
#p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
scale_x_discrete()

ggsave(filename=paste("plots/", experiment_name, "-max_min_delta.pdf", sep=''), plot=p1)
ggsave(filename=paste("plots/", experiment_name, "-histogram_delta.pdf", sep=''), plot=p2)
ggsave(filename=paste("plots/", experiment_name, "-points_delta.pdf", sep=''), plot=p3)
ggsave(filename=paste("plots/", experiment_name, "-histogram_services_engaged.pdf", sep=''), plot=p4)
ggsave(filename=paste("plots/", experiment_name, "-histogram_avg_execution_times_factories.pdf", sep=''), plot=p5)

experiment_data <- agg(experiment_name="e1t2", number_of_runs=1);
experiment_name <- "e1t2"
sub_title <- paste(", Experiment:", experiment_name)
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
#factories <- read.csv(file="factories-trial1.csv")
#p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
scale_x_discrete()

ggsave(filename=paste("plots/", experiment_name, "-max_min_delta.pdf", sep=''), plot=p1)
ggsave(filename=paste("plots/", experiment_name, "-histogram_delta.pdf", sep=''), plot=p2)
ggsave(filename=paste("plots/", experiment_name, "-points_delta.pdf", sep=''), plot=p3)
ggsave(filename=paste("plots/", experiment_name, "-histogram_services_engaged.pdf", sep=''), plot=p4)
ggsave(filename=paste("plots/", experiment_name, "-histogram_avg_execution_times_factories.pdf", sep=''), plot=p5)


#
# Failure Percentage
#
# Percentage of failed service compositions in relation to the search probability of exploration ants
# add the number of messages as well...
#
d <- list()
for (i in c("e1t1", "e1t2" ,"e1t3","e1t5", "e1t6", "e1t7", "e1t8", "e1t9", "e1t10")) {
  d[i] <- 1 - nrow(agg(experiment_name=i, number_of_runs=1)$raw)/100
}
failure_data_e1t1 <- data.frame(index=c(1,2,3,5,6,7,8,9,10), failure_percentage=unlist(d))

d <- list()
#  ,"e1t2_1000_03","e1t2_1000_04", "e1t2_1000_05", "e1t2_1000_06", "e1t2_1000_07", "e1t2_1000_08", "e1t2_1000_09", "e1t2_1000_10")) {
for (i in c("e1t2_1000_01", "e1t2_1000_02", "e1t2_1000_03", "e1t2_1000_04", "e1t2_1000_05")) {
  d[i] <- 1 - nrow(agg(experiment_name=i, number_of_runs=1)$raw)/1000
}
failure_data_e1t2 <- data.frame(index=c(1,2, 3, 4, 5), failure_percentage=unlist(d))


title <- paste("Successful Service Compositions using Sampling in ExplorationAnts", sub_title)
plot_e1t1 <- geom_line(data=failure_data_e1t1, aes(x=index, y=failure_percentage, color='100c10f'), size=2)
plot_e1t2 <- geom_line(data=failure_data_e1t2, aes(x=index, y=failure_percentage, color='1000c100f'), size=2)
ggplot() +  plot_e1t1 + plot_e1t2 +
  labs(title=title, x="ExplorationAnt Sampling Probability (%)", y="Failed Compositions (%)", colour="Network Composition") +
  scale_x_discrete(breaks=c(1,2,3,5,6,7,8,9,10), labels=c(10,20,30,50,60,70,80,90,100)) +
  scale_colour_manual(breaks=c("100c10f", "1000c100f"), labels=c("100 Clients, 10 Services", "1000 Clients, 100 Services"), values=c("100c10f" = "blue", "1000c100f" = "red"))

