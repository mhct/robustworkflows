#
# Creates a summary of a experiment trial
# Generates plots to describe the trial data
#

#
# Experiment 7, trial 1
#
experiment_data <- agg(experiment_name="e7t1", number_of_runs=9);
sub_title <- ", Experiment: e7t1"
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
factories <- read.csv(file="factories-trial1.csv")
p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
  scale_x_discrete()

ggsave(filename="plots/e7t1-max_min_delta.pdf", plot=p1)
ggsave(filename="plots/e7t1-histogram_delta.pdf", plot=p2)
ggsave(filename="plots/e7t1-points_delta.pdf", plot=p3)
ggsave(filename="plots/e7t1-histogram_services_engaged.pdf", plot=p4)
ggsave(filename="plots/e7t1-histogram_avg_execution_times_factories.pdf", plot=p5)


#
# trial 5
#
experiment_data <- agg(experiment_name="e7t5", number_of_runs=29);
sub_title <- ", Experiment: e7t5"
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
factories <- read.csv(file="factories-trial5.csv")
p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
  scale_x_discrete()


ggsave(filename="plots/e7t5-max_min_delta.pdf", plot=p1)
ggsave(filename="plots/e7t5-histogram_delta.pdf", plot=p2)
ggsave(filename="plots/e7t5-points_delta.pdf", plot=p3)
ggsave(filename="plots/e7t5-histogram_services_engaged.pdf", plot=p4)
ggsave(filename="plots/e7t5-histogram_avg_execution_times_factories.pdf", plot=p5)

#
# trial 5 - sampling
#
experiment_data <- agg(experiment_name="e7t5-sampling", number_of_runs=1);
sub_title <- ", Experiment: e7t5-sampling"
p1 <- plotLines(experiment_data$agg, sub_title);
p2 <- summDelta(experiment_data$agg, sub_title);
p3 <- plotErrors(experiment_data$agg, sub_title)
p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
factories <- read.csv(file="factories-trial5.csv")
p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
  scale_x_discrete()


ggsave(filename="plots/e7t5-samp-max_min_delta.pdf", plot=p1)
ggsave(filename="plots/e7t5-samp-histogram_delta.pdf", plot=p2)
ggsave(filename="plots/e7t5-samp-points_delta.pdf", plot=p3)
ggsave(filename="plots/e7t5-samp-histogram_services_engaged.pdf", plot=p4)
ggsave(filename="plots/e7t5-samp-histogram_avg_execution_times_factories.pdf", plot=p5)
