In terms of socket,it's important of these three workflow.
1. wait/going connection - connected
2. write data to endpoint
3. read data from endpoint

Surrounding those workflow, we able to build a fledged communication
we should care about:
1. encode/decode data which from application or transfer to application
2. message queue - socket communicate operation is single thread but application is multi-thread, so its important to define a
queue to let socket deal with data step by step.
3. streams - after these operation clicked(触发),we need a RX programming style(may be others) make logic lighter.
4. manage all of sockets, contains close/search/send or write data with specify socket(s).