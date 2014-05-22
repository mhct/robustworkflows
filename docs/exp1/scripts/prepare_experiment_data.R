
library(ggplot2)
library(reshape)

#
# Gets an experiment name, checks for the experiments and create a data.frame
# containing the information of such experiments and the aggregate form as well
#
# @param experiment_name, String containing the path and name of basic experiment,
#                   eg: "data/250"
# @param number_of_runs, Integer specifying how many experiments there are 
#
runexp <- function(experiment_name, number_of_runs) {
  
  # assuming data in CSV format: time_block,EXPECTED_TIME_TO_SERVE_COMPOSITION,REAL_TIME_TO_SERVE_COMPOSITION,CLIENT_AGENT,SERVICES_ENGAGED, start_time
  raw_data <- read.csv(file=experiment_name, as.is=TRUE)
  raw_data <- data.frame(X=1, raw_data)
  raw_data <- data.frame(raw_data, delta=(raw_data$REAL_TIME_TO_SERVE_COMPOSITION-raw_data$EXPECTED_TIME_TO_SERVE_COMPOSITION)/1000)
  sorted_data <- raw_data[order(raw_data[,5]),]
  
  return(sorted_data)
  #agg_data_mean <- aggregate(data=raw_data, cbind(EXPECTED_TIME_TO_SERVE_COMPOSITION,REAL_TIME_TO_SERVE_COMPOSITION, delta) ~ X, mean)
}

#
# Converts a CSV file from an experiment, to a data.frame containing both, the 
# raw data and the aggregated version of the data.
# 
# An experiment consists of a CSV file having multiple rows. Each row corresponds to a run
#
# @returns list(raw, agg) a list with the RAW data, and the Aggregated data
#
agg <- function(experiment_name, number_of_runs) {
  # assuming data in CSV format: time_block,EXPECTED_TIME_TO_SERVE_COMPOSITION,REAL_TIME_TO_SERVE_COMPOSITION,CLIENT_AGENT,SERVICES_ENGAGED, run
  raw_data <- read.csv(file=experiment_name, as.is=TRUE)
  
  #TODO HACK for data without run info
  raw_data$run <- 0
  
  #
  # adds x column to data frame
  #
  raw_data <- data.frame(X=1, raw_data)

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

plotHistogram <- function(data_to_plot, title_data) {
  plot <- ggplot(data=data_to_plot) + geom_histogram(aes(x=Freq, y=..density..), color= "black", binwidth=5) + 
    agg
}

factories_service_times <- function(data, title) {
  title <- paste("Service computation times - ", title)
  plot <- ggplot(d250.factories, aes(ServiceType, time/1000)) + geom_boxplot() +
    labs(title=title, x="Service Type", y="Execution Time (s)")
}
plotLines <- function(data_to_plot, sub_title) {
  title <- paste("Composition times", sub_title)
  l1 <- geom_line(data=data_to_plot, aes(x=X, y=REAL_TIME_TO_SERVE_COMPOSITION/1000, color='Real'), size=1)
  l2 <- geom_line(data=data_to_plot, aes(x=X, y=EXPECTED_TIME_TO_SERVE_COMPOSITION/1000, color='Expected'), size=1)
  l3 <- geom_line(data=data_to_plot, aes(x=X, y=(REAL_TIME_TO_SERVE_COMPOSITION-EXPECTED_TIME_TO_SERVE_COMPOSITION)/1000, color='Delta'), size=1)
  
  plot <- ggplot() + l1 + l2 + l3 + scale_colour_manual(breaks=c("Delta", "Expected", "Real"), values=c("black","blue","red")) + 
    labs(title=title, x="Execution number", y="Composition time (s)")
  
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

plotRealCompositionTimes <- function(data_to_plot, sub_title, bin_width_data=50) {
  title <- paste("Average Time to Execute Compositions", sub_title)
  plot <- ggplot(data=data_to_plot) + geom_histogram(aes(x=REAL_TIME_TO_SERVE_COMPOSITION/1000, y=..density..), color="black", binwidth=bin_width_data) + 
    labs(title=title, x="Real Composition time (s)", y="Density")
  
  return(plot)
}

#
# Calculates the SMAPE of the given data
# STATISTICS SMAPE (http://en.wikipedia.org/wiki/Symmetric_mean_absolute_percentage_error)
#
# @experiment_data a data.frame having at least the following columns ['EXPECTED_TIME_TO_SERVE_COMPOSITION','REAL_TIME_TO_SERVE_COMPOSITION']
#
# @returns Float, a real number between [0.00, 1.00] indicating how close the Real columns was to the Expected columns 
#                 0.00 => exact match, 1.00 => extremelly large deviation
#
smape <- function(experiment_data) {
  a <- experiment_data
  sum(abs(a$EXPECTED_TIME_TO_SERVE_COMPOSITION - a$REAL_TIME_TO_SERVE_COMPOSITION))/sum(a$REAL_TIME_TO_SERVE_COMPOSITION+a$EXPECTED_TIME_TO_SERVE_COMPOSITION)
}

analysisSmape <- function(md) {
  d <- list()
  maximumDistance <- md
  for ( i in seq(1:maximumDistance)) {
    a <- data.frame(EXPECTED_TIME_TO_SERVE_COMPOSITION=rep(1, times=1), REAL_TIME_TO_SERVE_COMPOSITION=rep((1+i/100)*1,times=1))
    d[i] <- smape(a)
  }
  
  smapes <- data.frame(x=seq(1:maximumDistance), y=unlist(d))
  ggplot(smapes, aes(x=x, y=y)) +
    geom_line() +
     labs(title="SMAPE asymptotic values", x="Distance between values", y="SMAPE value")
  
}
#dt <- data.frame(smape=matrix(unlist(t[2:6]), nrow=6, byrow=T))
#ggplot(data=dt, aes(x=seq(1:6), y=smape)) + geom_point()

##########################################################################
##########################################################################
#
# Computes the Vargha-Delaney A measure for two populations a and b.
#
# Equation numbers below refer to the paper:
# @article{vargha2000critique,
#  title={A critique and improvement of the CL common language effect size
#               statistics of McGraw and Wong},
#  author={Vargha, A. and Delaney, H.D.},
#  journal={Journal of Educational and Behavioral Statistics},
#  volume={25},
#  number={2},
#  pages={101--132},
#  year={2000},
#  publisher={Sage Publications}
# }
# 
# a: a vector of real numbers
# b: a vector of real numbers 
# Returns: A real number between 0 and 1 
AMeasure <- function(a,b){
  
  # Compute the rank sum (Eqn 13)
  r = rank(c(a,b))
  r1 = sum(r[seq_along(a)])
  
  # Compute the measure (Eqn 14) 
  m = length(a)
  n = length(b)
  A = (r1/m - (m+1)/2)/n
  
  A
}