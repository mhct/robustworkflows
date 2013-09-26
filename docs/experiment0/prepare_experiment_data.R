
library(ggplot2)
agg <- function(experiment_name) {
  # assuming data in CSV format: time_block,EXPECTED_TIME_TO_SERVE_COMPOSITION,REAL_TIME_TO_SERVE_COMPOSITION,CLIENT_AGENT,SERVICES_ENGAGED
  raw_data <- read.csv(file=experiment_name)
  agg_data_time <- aggregate(data=raw_data, cbind(EXPECTED_TIME_TO_SERVE_COMPOSITION,REAL_TIME_TO_SERVE_COMPOSITION) ~ time_block, mean)
  
  #agg_data_services_engaged <- aggregate(data=raw_data, cbind()
  
  #bla <- as.data.frame(table(raw_data$CLIENT_AGENT))
  
  raw_data <- data.frame(X=seq(1:nrow(raw_data)), raw_data, delta=(raw_data$REAL_TIME_TO_SERVE_COMPOSITION-raw_data$EXPECTED_TIME_TO_SERVE_COMPOSITION)/1000)
  
  return(list("raw" = raw_data, "agg" = agg_data_time))
}

plotHistogram <- function(data_to_plot) {
  plot <- ggplot(data=data_to_plot) + geom_histogram(aes(x=Freq, y=..density..), color= "black", binwidth=5) + 
    labs(title="Executed compositions per service", x="Number of Compositions", y="Density of services")
}

plotLines <- function(data_to_plot) {
  l1 <- geom_line(data=data_to_plot, aes(x=X, y=REAL_TIME_TO_SERVE_COMPOSITION/1000, color='Real'), size=2)
  l2 <- geom_line(data=data_to_plot, aes(x=X, y=EXPECTED_TIME_TO_SERVE_COMPOSITION/1000, color='Expected'), size=2)
  l3 <- geom_line(data=data_to_plot, aes(x=X, y=(REAL_TIME_TO_SERVE_COMPOSITION-EXPECTED_TIME_TO_SERVE_COMPOSITION)/1000, color='Delta'), size=2)
  
  ggplot() + l1 + l2 + l3 + scale_colour_manual(breaks=c("Delta", "Expected", "Real"), values=c("black","blue","red"))
  
}