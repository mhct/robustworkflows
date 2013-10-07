
library(ggplot2)

agg <- function(experiment_name, number_of_runs) {
  # assuming data in CSV format: time_block,EXPECTED_TIME_TO_SERVE_COMPOSITION,REAL_TIME_TO_SERVE_COMPOSITION,CLIENT_AGENT,SERVICES_ENGAGED, run
  raw_data <- read.csv(file=experiment_name)
  
  #
  # adds x column to data frame
  #
  raw_data <- data.frame(X=1, raw_data)

  #
  # adds sequence of events to the data from each run
  #
  for (i in seq(1:number_of_runs)) {
    raw_data[raw_data$run == i-1,]$X <- seq(1:(nrow(raw_data)/number_of_runs))
  }

  #
  # Calculates delta, between expected and real execution times
  #
  raw_data <- data.frame(raw_data, delta=(raw_data$REAL_TIME_TO_SERVE_COMPOSITION-raw_data$EXPECTED_TIME_TO_SERVE_COMPOSITION)/1000)

  #agg_data_services_engaged <- aggregate(data=raw_data, cbind()
  
  #bla <- as.data.frame(table(raw_data$CLIENT_AGENT))

  agg_data <- aggregate(data=raw_data, cbind(EXPECTED_TIME_TO_SERVE_COMPOSITION,REAL_TIME_TO_SERVE_COMPOSITION, delta) ~ X, mean)
  #return(list("raw" = raw_data, "agg" = agg_data_time, "frequency" = frequency_delta))
  return(list("raw" = raw_data, "agg" = agg_data))
}

plotHistogram <- function(data_to_plot) {
  plot <- ggplot(data=data_to_plot) + geom_histogram(aes(x=Freq, y=..density..), color= "black", binwidth=5) + 
    labs(title="Executed compositions per service", x="Number of Compositions", y="Density of services")
}

plotLines <- function(data_to_plot) {
  l1 <- geom_line(data=data_to_plot, aes(x=X, y=REAL_TIME_TO_SERVE_COMPOSITION/1000, color='Real'), size=2)
  l2 <- geom_line(data=data_to_plot, aes(x=X, y=EXPECTED_TIME_TO_SERVE_COMPOSITION/1000, color='Expected'), size=2)
  l3 <- geom_line(data=data_to_plot, aes(x=X, y=(REAL_TIME_TO_SERVE_COMPOSITION-EXPECTED_TIME_TO_SERVE_COMPOSITION)/1000, color='Delta'), size=2)
  
  ggplot() + l1 + l2 + l3 + scale_colour_manual(breaks=c("Delta", "Expected", "Real"), values=c("black","blue","red")) + 
    labs(title="Avg Time to execute compositions", x="Execution bumber", y="Composition time (ms)")
  
  
}

summDelta <- function(data_to_plot) {
  ggplot(data=data_to_plot) + geom_histogram(aes(x=delta, y=..density..), color="black", binwidth=50) + 
    labs(title="Average Compositions Times (Real - Expected)", x="Time to execute composition (s)", y="Density")
}

#
# Creates a summary of a experiment trial
# Generates plots to describe the trial data
#
trialSummary <- function(exp_name, number_of_runs) {
  experiment_data <- agg(exp_name, number_of_runs);
  summDelta(experiment_data$agg);
  #plotLines(experiment_data$agg);
  
}