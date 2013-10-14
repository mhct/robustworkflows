
library(ggplot2)
library(reshape)

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

  agg_data_mean <- aggregate(data=raw_data, cbind(EXPECTED_TIME_TO_SERVE_COMPOSITION,REAL_TIME_TO_SERVE_COMPOSITION, delta) ~ X, mean)

  #
  # Calculates Error
  #
  agg_data_sd <- aggregate(data=raw_data, cbind(EXPECTED_TIME_TO_SERVE_COMPOSITION,REAL_TIME_TO_SERVE_COMPOSITION, delta) ~ X, sd)
  
  error <- calculateError(agg_mean=agg_data_mean, agg_sd=agg_data_sd, number_of_runs=number_of_runs)
  error <- error$data
  agg_data <- data.frame(agg_data_mean, delta_ymin=error$ymin, delta_ymax=error$ymax)
  
  #return(list("raw" = raw_data, "agg" = agg_data_time, "frequency" = frequency_delta))
  return(list("raw" = raw_data, "agg" = agg_data))
}

#
# Calculates error of DELTA
#
calculateError <- function(agg_mean, agg_sd, number_of_runs) {
  error <- qt(0.975, df=number_of_runs-1) * agg_sd$delta / sqrt(number_of_runs)
  ymin <- agg_mean$delta - error
  ymax <- agg_mean$delta + error
  
  histogram_data <- data.frame(delta=agg_mean$delta, ymin=ymin, ymax=ymax)
  
  return (list("data" = histogram_data))
}

plotHistogram <- function(data_to_plot) {
  plot <- ggplot(data=data_to_plot) + geom_histogram(aes(x=Freq, y=..density..), color= "black", binwidth=5) + 
    labs(title="Executed compositions per service", x="Number of Compositions", y="Density of services")
}

plotLines <- function(data_to_plot, sub_title) {
  title <- paste("Composition times", sub_title)
  l1 <- geom_line(data=data_to_plot, aes(x=X, y=REAL_TIME_TO_SERVE_COMPOSITION/1000, color='Real'), size=2)
  l2 <- geom_line(data=data_to_plot, aes(x=X, y=EXPECTED_TIME_TO_SERVE_COMPOSITION/1000, color='Expected'), size=2)
  l3 <- geom_line(data=data_to_plot, aes(x=X, y=(REAL_TIME_TO_SERVE_COMPOSITION-EXPECTED_TIME_TO_SERVE_COMPOSITION)/1000, color='Delta'), size=2)
  
  plot <- ggplot() + l1 + l2 + l3 + scale_colour_manual(breaks=c("Delta", "Expected", "Real"), values=c("black","blue","red")) + 
    labs(title=title, x="Execution number", y="Composition time (ms)")
  
  return(plot)
}

plotErrors <- function(data_to_plot, sub_title) {
  title <- paste("Difference between Real and Expected composition times", sub_title)
  plot <- ggplot(data=data_to_plot, aes(x=seq(1:length(delta)), y=delta)) + geom_point() + geom_errorbar(aes(ymin=delta_ymin, ymax=delta_ymax)) +
  labs(title = title, x = "Emulation step (unit)", y = "Delta (Real - Expected composition times) (s)")
  
  return(plot)
}

summDelta <- function(data_to_plot, sub_title) {
  title <- paste("Average Composition Times (Real - Expected)", sub_title)
  plot <- ggplot(data=data_to_plot) + geom_histogram(aes(x=delta, y=..density..), color="black", binwidth=50) + 
    labs(title=title, x="Composition time (s)", y="Density")
  
  return(plot)
}

