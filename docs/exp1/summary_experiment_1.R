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



