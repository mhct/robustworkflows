library(ggplot2)
agg <- function(experiment_name) {
  # assuming data in CSV format: time_block,EXPECTED_TIME_TO_SERVE_COMPOSITION,REAL_TIME_TO_SERVE_COMPOSITION,CLIENT_AGENT,SERVICES_ENGAGED
  raw_data <- read.csv(file=experiment_name)
  agg_data_time <- aggregate(data=raw_data, cbind(EXPECTED_TIME_TO_SERVE_COMPOSITION,REAL_TIME_TO_SERVE_COMPOSITION) ~ time_block, mean)
  
  #agg_data_services_engaged <- aggregate(data=raw_data, cbind()
  
  bla <- as.data.frame(table(raw_data$CLIENT_AGENT))
  plot <- ggplot(data=bla) + geom_histogram(aes(x=Freq, y=..density..), color= "black", binwidth=5) + labs(title="Executed compositions per service", x="Number of Compositions", y="Density of services")
 
  return(list("raw" = raw_data, "agg" = agg_data_time, "plot" = plot))
}