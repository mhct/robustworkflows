#
# Creates a summary of a experiment trial
# Script tailored to the hypothesis 1 H1, check if the number of nodes influence on 
# the time to execute the service compositions
#

#
# BoxPlot to compare the Average Composition different network sizes
#
unset(distribution_compositions)
trials <- c("t250", "t1000", "t4000")
agg_data <- data.frame()
i <- 1
for (trial in trials) {
  agg_temp <- agg(experiment_name=paste("raw_results_data/", trial, sep=''), number_of_runs=1)$raw
  agg_temp$X <- i
  i <- i+1
  agg_data <- rbind(agg_data, agg_temp)
}

distribution_compositions <- ggplot(data=agg_data, aes(factor(X), delta)) + geom_boxplot() + 
  scale_x_discrete(labels=c("1" = "250", "2" = "1000", "3" = "4000")) +
  labs(title="Delta in expected versus real composition time", x="Number of Component Services", y="Mean delta (Real - Expected) Composition Duration (s)")
distribution_compositions

#ggsave(filename="plots/distribution_expected_composition_times100c_10s.pdf", plot=distribution_compositions)
#distribution_compositions <- ggplot(data=agg_data, aes(factor(X), REAL_TIME_TO_SERVE_COMPOSITION/1000)) + geom_boxplot() + 
#  labs(title="Distribution of REAL Composition times by number of concurrent monitoring requests, 100 Clients, 10 Servers", x="Concurrent requests (number)", y="Composition duration (s)")
#ggsave(filename="plots/distribution_real_composition_times100c_10s.pdf", plot=distribution_compositions)



#
# Average execution time of each component service
#
#services_time <- read.csv(file="factories-exp1t3.csv")
#service_distribution_times <- ggplot(data=services_time, aes(factor(ServiceType), time/1000)) + geom_boxplot() + 
#  labs(title="Component Services, Avg. Execution time. 2000 Component Services", x="Service type", y="Average execution duration (s)")
#ggsave(filename="plots/compserv_avg_time_exp1t3.pdf", plot=service_distribution_times)

#services_time <- read.csv(file="fac_t1.csv")
#service_distribution_times <- ggplot(data=services_time, aes(factor(ServiceType), time/1000)) + geom_boxplot() + 
#  labs(title="Component Services, Avg. Execution time. PASTE FUnCTION Component Services", x="Service type", y="Average execution duration (s)")
#ggsave(filename="plots/compserv_avg_time_exp1t1.pdf", plot=service_distribution_times)


#
# BoxPlot to compare the Average Composition time for different concurrency levels
# TRIAL 1 100 clients, 10 factories
#
#executions <- 5
#agg_data <- data.frame()
#x <- "abcdefghijk"
#for (i in seq(1:executions)) {
#  agg_temp <- agg(experiment_name=paste("results_trial1/me1t1", substr(x, start=i, stop=i), sep=''), number_of_runs=1)$raw
#  agg_temp$X <- i
  
#  agg_data <- rbind(agg_data, agg_temp)
#}
#distribution_compositions <- ggplot(data=agg_data, aes(factor(X), EXPECTED_TIME_TO_SERVE_COMPOSITION/1000)) + geom_boxplot() + 
#  labs(title="Distribution of Expected Composition times by number of concurrent monitoring requests, 100 Clients, 10 Servers", x="Concurrent requests (number)", y="Composition duration (s)")
#ggsave(filename="plots/distribution_expected_composition_times100c_10s.pdf", plot=distribution_compositions)
#distribution_compositions <- ggplot(data=agg_data, aes(factor(X), REAL_TIME_TO_SERVE_COMPOSITION/1000)) + geom_boxplot() + 
#  labs(title="Distribution of REAL Composition times by number of concurrent monitoring requests, 100 Clients, 10 Servers", x="Concurrent requests (number)", y="Composition duration (s)")
#ggsave(filename="plots/distribution_real_composition_times100c_10s.pdf", plot=distribution_compositions)


#
# BoxPlot to compare the Average Composition time for different concurrency levels
# TRIAL 2 1000 clients, 100 factories
#
executions <- 4
agg_data <- data.frame()
x <- "abcdefghijk"
for (i in seq(1:executions)) {
  agg_temp <- agg(experiment_name=paste("results_trial1/me1t2", substr(x, start=i, stop=i), sep=''), number_of_runs=1)$raw
  agg_temp$X <- i
  
  agg_data <- rbind(agg_data, agg_temp)
}
distribution_compositions <- ggplot(data=agg_data, aes(factor(X), EXPECTED_TIME_TO_SERVE_COMPOSITION/1000)) + geom_boxplot() + 
  labs(title="Distribution of Expected Composition times by number of concurrent monitoring requests, 1000 Clients, 100 Servers", x="Concurrent requests (number)", y="Composition duration (s)")
ggsave(filename="plots/distribution_expected_composition_times1000c_100s.pdf", plot=distribution_compositions)

distribution_compositions <- ggplot(data=agg_data, aes(factor(X), REAL_TIME_TO_SERVE_COMPOSITION/1000)) + geom_boxplot() + 
  labs(title="Distribution of REAL Composition times by number of concurrent monitoring requests, 1000 Clients, 100 Servers", x="Concurrent requests (number)", y="Composition duration (s)")
ggsave(filename="plots/distribution_real_composition_times1000c_100s.pdf", plot=distribution_compositions)


#
# BoxPlot to compare the Average Composition time for different concurrency levels
# TRIAL 3 10000 clients, 100 factories
#
executions <- 3
agg_data <- data.frame()
x <- "abcdefghijk"
for (i in seq(1:executions)) {
  agg_temp <- agg(experiment_name=paste("results_trial1/me1t3", substr(x, start=i, stop=i), sep=''), number_of_runs=1)$raw
  agg_temp$X <- i
  
  agg_data <- rbind(agg_data, agg_temp)
}
distribution_compositions <- ggplot(data=agg_data, aes(factor(X), EXPECTED_TIME_TO_SERVE_COMPOSITION/1000)) + geom_boxplot() + 
  labs(title="Distribution of Expected Composition times by number of concurrent monitoring requests, 10000 Clients, 1000 Servers", x="Concurrent requests (number)", y="Composition duration (s)")
ggsave(filename="plots/distribution_expected_composition_times10000c_1000s.pdf", plot=distribution_compositions)

distribution_compositions <- ggplot(data=agg_data, aes(factor(X), REAL_TIME_TO_SERVE_COMPOSITION/1000)) + geom_boxplot() + 
  labs(title="Distribution of REAL Composition times by number of concurrent monitoring requests, 10000 Clients, 1000 Servers", x="Concurrent requests (number)", y="Composition duration (s)")
ggsave(filename="plots/distribution_real_composition_times10000c_1000s.pdf", plot=distribution_compositions)



#
# SMAPE to show the error in estimates of the expected composition time, for different concurrency levels
#
t <- list()
for (i in seq(1:executions)) {
  t[i] <- smape(agg(experiment_name=paste("t3-", i, sep=''), number_of_runs=1)$raw)
}
experiment1_trial1 <- data.frame(smape=matrix(unlist(t), nrow=executions, byrow=T))
smapeTrial1 <- ggplot(data=experiment1_trial1, aes(x=seq(1:executions), y=100*smape)) + geom_point(size=2) +
  scale_x_discrete(breaks=seq(1:executions), labels=seq(1:executions)) +
  labs(title="SMAPE by concurrency, 100 Clients, 10 Servers", x="Number of concurrent requests", y="SMAPE result (%)")

ggsave(filename=paste("plots/", "smape2-trial1.pdf", sep=''), plot=smapeTrial1)




#
# Detailed information about each concurrent execution
#
for (i in seq(1:executions)) {
  experiment_name <- paste("t3-",i,sep='')
  experiment_data <- agg(experiment_name=experiment_name, number_of_runs=1);
  
  sub_title <- paste(", Experiment:", experiment_name)
  p1 <- plotLines(experiment_data$agg, sub_title);
  p2 <- summDelta(experiment_data$agg, sub_title);
  pCompSummary <- plotRealCompositionTimes(experiment_data$agg, sub_title);
  p3 <- plotErrors(experiment_data$agg, sub_title)
  p4 <- ggplot(data=experiment_data$raw) + geom_histogram(aes(x=SERVICES_ENGAGED)) + labs(title=paste("Services Used in the compositions", sub_title))
  #factories <- read.csv(file="factories-trial1.csv")
  #p5 <- ggplot(data=factories) + geom_bar(aes(x=seq(1:10), y=time/1000), stat="identity") + labs(title=paste("Average service execution time of component services", sub_title), x="Component Service Identification", y="Average execution time (s)") +
  #scale_x_discrete()
  
  ggsave(filename=paste("plots/", experiment_name, "-max_min_delta.pdf", sep=''), plot=p1)
  ggsave(filename=paste("plots/", experiment_name, "-histogram_delta.pdf", sep=''), plot=p2)
  ggsave(filename=paste("plots/", experiment_name, "-points_delta.pdf", sep=''), plot=p3)
  ggsave(filename=paste("plots/", experiment_name, "-histogram_services_engaged.pdf", sep=''), plot=p4)
  #ggsave(filename=paste("plots/", experiment_name, "-histogram_avg_execution_times_factories.pdf", sep=''), plot=p5)
  ggsave(filename=paste("plots/", experiment_name, "-histogram_avg_real_execution_time_composition.pdf", sep=''), plot=pCompSummary)

}

#
# Analysis of data
#
a <- data.frame(x=1:100)

for (j in 1:100) { 
  for ( i in 1:100) { 
    dd[i] <- mean(sample(x,i))
  } 
  a <- cbind(a, unlist(dd))
}


#
# creating histogram to check the frequency of the used factories
#
