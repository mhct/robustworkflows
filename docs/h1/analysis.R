#
# Analysis for hypothesis 1, data.
# 
# 19/05/2014
# Mario h.c.t.
#

# source('prepare_experiment_data.R') TODO find out a good folder schema

#
# Calculates the relative time (in milliseconds), between each row
# 
# @param dataframe having a column "start_time_millis", containing milliseconds data
# 
# @returns a copy of the dataframe with the column "start_time_millis" having relative values between
#          the rows
#
relativeTime <- function(dataframe) {
  dataframe <- dataframe[order(dataframe[, c("start_time_millis")]),]
  reference_value <- dataframe[1,c("start_time_millis")]
  dataframe[,c("start_time_millis")] <- dataframe[,c("start_time_millis")] - reference_value  
  dataframe
}

#
# Counts the number of services engaged. Services are given by the number of ";"
# in a string
#
# @param a string having data in the form ";234;234;234"
#
# @returns an integer with the number of services engaged
#
nbServicesEngaged <- function(data) {
  nbServices <- length(unlist(strsplit(data, ";"))) - 1 
}

#################################################################
# Start Analysis experiment
#
#################################################################
  #experiment_name<-"data/h1_1k_1"
  #title <-  "1000 Nodes: 96 Composite Services, 904 Component Services"
  #filename_suffix <- "d1k"

  experiment_name<-"h1_250"
  title<-"250 Nodes: 27 Composite Services, 223 Component Services"
  filename_suffix<-"d250"
  
  #experiment_name<-"data/h1_4k_1_2"
  #title<-"4000 Nodes: 431 Composite Services, 3569 Component Services"
  #filename_suffix<-"d4k_2"
  
  #experiment_name<-"data/h1_16k_1"
  #title<-"16000 Nodes: 1523 Composite Services, 14367 Component Services"
  #filename_suffix<-"d16k_1"

  experiment_name<-"data/h1_64k_1"
  title<-"64000 Nodes: 6456 Composite Services, 57544 Component Services"
  filename_suffix<-"d64k_1"

  d250 <- runexp(experiment_name=experiment_name)
  d250.title <- title
  d250.filename_suffix <- filename_suffix
  
  d250_raw <- d250$raw
  d250_ordered <- relativeTime(d250_raw[order(d250_raw[,c("start_time_millis")]),])
  


  #
  # Generate boxplots describing the average execution times of each factory (Component Service)
  #
  #d250.factories <- read.csv(paste("data/", filename_suffix, "_factories.csv", sep=""))
  #p <- factories_service_times(d250.factories, d250$title)
  #ggsave(filename=paste("plots/",d250.filename_suffix,"_factories.pdf", sep=""), plot=p)
  
  
  #
  # Analysis of Average composition time given the size of the composition
  #
  #factorNbServices <- factor(unlist(lapply(d250_ordered$SERVICES_ENGAGED, nbServicesEngaged)))
  #assign("factorNbServices", factorNbServices, pos = globalenv())
  d250.avg_composition_time_plot <- ggplot(data=d250_ordered, aes(x=start_time_millis, y=REAL_TIME_TO_SERVE_COMPOSITION/1000, colour=factor(composition_size))) +
      geom_point() +
      geom_smooth() +
      labs(title=paste(d250.title,"\nAverage Composition Time by Composition Size"),
           x="Time (ms)",
           y="Composition Time (s)")
      scale_color_hue("Tasks per\nComposite Service")
  
  d250.delta_plot <- ggplot(d250_ordered, aes(x=d250_ordered$start_time_millis, 
                                              y=d250_ordered$delta/(d250_ordered$REAL_TIME_TO_SERVE_COMPOSITION/1000), colour=factorNbServices)) + 
    geom_point() + 
    geom_smooth() + 
    labs(title=paste(d250.title, "\nEstimate difference, REAL and Expected composition times, by nbServices"), 
         x="Time (ms)",
         y="Percentage (0.00 - 1.00)") + 
    scale_color_hue("Tasks per\nComposite Service")
  
  ggsave(filename=paste("plots/",d250.filename_suffix,"_avg_composition_time.pdf", sep=""), plot=d250.avg_composition_time_plot)
  ggsave(filename=paste("plots/",d250.filename_suffix,"_delta.pdf", sep=""), plot=d250.delta_plot)
  
  
  
  
  #
  # Normalizes the data
  # 
  #means <- tapply(d250.ordered$REAL_TIME_TO_SERVE_COMPOSITION, factorNbServices, mean)
  #normalized_data <- scale(d250.ordered$REAL_TIME_TO_SERVE_COMPOSITION)[,1]
  
  #update data.frame
  #d250.ordered <- cbind(d250.ordered, normalized_real_time=normalized_data)
  
  d250_ordered <- cbind(d250_ordered, composition_size=factorNbServices)
  d250_c2 <- d250_ordered[d250_ordered$composition_size == 2,]
  d250_c3 <- d250_ordered[d250_ordered$composition_size == 3,]
  d250_c3 <- cbind(d250_c3, norm_real_time=scale(d250_c3$REAL_TIME_TO_SERVE_COMPOSITION))
  d250_c2 <- cbind(d250_c2, norm_real_time=scale(d250_c2$REAL_TIME_TO_SERVE_COMPOSITION))
  d250_merged <- rbind(d250_c2, d250_c3)
  
  d250_merged <- b
  d250.faceted_hist_compositions_plot <- ggplot(d250_merged) + 
    geom_histogram(aes(x=d250_merged$norm_real_time, y=..density..), binwidth=0.2) + 
    facet_grid(. ~ composition_size) +
    labs(title=paste(d250.title, "\nNormalized Composition Time, by Composition Size"), 
         x="Distribution Composition Time",
         y="Percentage (0.00 - 1.00)")
  
  ggsave(filename=paste("plots/",d250.filename_suffix,"_faceted_hist.pdf", sep=""), plot=d250.faceted_hist_compositions_plot)
  rm(d250)
  rm(d250)
  rm(d250_c2)
  rm(d250_c3)
  rm(d250_merged)
  rm(d250_ordered)
  rm(d250_raw)
  rm(d250.factories)
  rm(d250.avg_composition_time_plot, d250.delta_plot, d250.faceted_hist_compositions_plot, d250.filename_suffix, factorNbServices)




############################################################
#
# General Summary, execution time, per size of network.
# Creates Box plot showing the composition time means, per size of the network
#
############################################################
experiment_names <- c("h1_250", "h1_1k", "h1_4k", "h1_16k", "h1_64k")

experiment_results_raw <- data.frame()
experiment_results_agg <- data.frame()
for ( experiment in experiment_names ) {
  d250 <- runexpSummary(experiment_name=experiment)
  experiment_results_raw <- rbind(experiment_results_raw, d250$raw)
  experiment_results_agg <- rbind(experiment_results_agg, d250$agg)
}

limits <- aes(ymax = real_composition_ymax/1000, ymin=real_composition_ymin/1000)

h1_composition_time_plot.labels <- c("250", "1k", "4k", "16k", "64k")
h1_composition_time_plot <- ggplot(experiment_results_agg, aes(x=factor(experiment), y=REAL_TIME_TO_SERVE_COMPOSITION/1000)) + geom_point() +
  facet_grid(. ~ composition_size) +
  labs(title="Average Composition Time for different number of nodes in the system", 
       x="Number of Nodes", 
       y="Composition Time (s)") +
  scale_x_discrete(labels=h1_composition_time_plot.labels) +
  geom_errorbar(limits)

h1_composition_time_plot  

#Zoomed in
ylim1 <- boxplot.stats(experiment_results_agg$REAL_TIME_TO_SERVE_COMPOSITION/1000)$stats[c(1, 5)]
h1_composition_time_plot_zoom <- h1_composition_time_plot + coord_cartesian(ylim = ylim1*1.05)

ggsave(filename="plots/h1_average_composition_time.pdf", 
       plot=h1_composition_time_plot,
       height=13,
       units="cm")

ggsave(filename="plots/h1_average_composition_time_zoom.pdf", 
       plot=h1_composition_time_plot_zoom,
       height=13,
       units="cm")


#
# 
# Showing the distribution as well, instead of errorbars
#
executions_data_summary <- aggregate(data=experiment_results_raw, cbind(EXPECTED_TIME_TO_SERVE_COMPOSITION,
                                                                 REAL_TIME_TO_SERVE_COMPOSITION, 
                                                                 delta, experiment) ~ X + composition_size, mean)


h1_composition_time_boxplot <- ggplot(executions_data_summary) + geom_boxplot(aes(x=factor(experiment), y=REAL_TIME_TO_SERVE_COMPOSITION/1000)) +
  facet_grid(. ~ composition_size) +
  labs(title="Average Composition Time for different number of nodes in the system", 
       x="Number of Nodes", 
       y="Composition Time (s)") +
  scale_x_discrete(labels=h1_composition_time_plot.labels)
h1_composition_time_boxplot
ggsave(filename="plots/h1_composition_time_boxplot.pdf",
       plot=h1_composition_time_boxplot,
       height=13,
       units="cm")


############################################################
#
# Failures per experiment
#
############################################################
# collected data manually
failure_data <- data.frame(failures=c(9,33,370,1239,6253), 
                       total=c(270, 960, 4310, 15230, 64560), 
                       experiment=c(1, 2, 3, 4, 5))

ggplot(failure_data) + geom_point(aes(x=experiment, y=failures/total)) +
  labs(title="Average Composition Time for different number of nodes in the system", 
       x="Number of Nodes", 
       y="Composition Time (s)") +
  scale_x_discrete(labels=h1_composition_time_plot.labels)

#
# Analysis of one particular Client Agent
#
d250_
d250_2 <- agg(experiment_name="data/h1_250_1", number_of_runs=1)
d250_2.raw <- d250_2$raw
d250_2.d_st <- d250_2.raw[order(d250_2.raw[,c("start_time")]),]
d250_2.c40 <- d250_2.d_st[d250_2.d_st$CLIENT_AGENT == "40",]

d250_3 <- agg(experiment_name="data/h1_250_3", number_of_runs=1)
d250_3.raw <- d250_3$raw
d250_3.d_st <- d250_3.raw[order(d250_3.raw[,c("start_time")]),]
d250_3.c40 <- d250_3.d_st[d250_3.d_st$CLIENT_AGENT == "40",]

#plotRealCompositionTimes(data_to_plot=d250$raw, d250$title, 40)
#plotRealCompositionTimes(data_to_plot=d64k$raw, "64k", 40)

ggplot() + geom_smooth(data=d250_2.c40, aes(x=1:10, REAL_TIME_TO_SERVE_COMPOSITION)) + 
  geom_smooth(data=c40, aes(x=1:10, REAL_TIME_TO_SERVE_COMPOSITION)) +
  geom_smooth(data=d250_3.c40, aes(x=1:10, REAL_TIME_TO_SERVE_COMPOSITION)) 

