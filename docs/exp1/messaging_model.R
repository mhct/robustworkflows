#
# Calculates the number of messages needed to exchange between clients and services, 
# to create an "local"optimal composition
#
# @param alfa, is the ration between servers/clients
# @param workflow_size, is the number of activities per workflow
# @param clients is the number of clients
# 
# @return a vector with the number of needed messages
#
m <- function(alpha, workflow_size, clients) {
  return (alpha * workflow_size * clients ^2)  
}

clients <- 1:50000
alpha <- 0.1

messages <- data.frame(X=clients)
for (i in seq(1,30)) {
  mtemp <- data.frame(m(alpha, i, clients))
  messages[paste("m", i)] <- mtemp
}

#
# generates plot objects
#
test <- melt(messages, id="X")
ggplot(data=test, aes(x=X, y=value, colour=variable)) + geom_line()