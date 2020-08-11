This code complex is  O(n + n^2) because I read all lines O(n) and also use foreach inside foreach - which leads to O(n^2). 
This algorithm doesn scale well, because of the complexity and because of the reading all lines at one to the memory.

If I had more time I would implement an algorithm based on the HashMap, when reading lines in the first run I would create HashMap where key is the rest of the sentence - 
and the value is LineGroup - an object that stores similar lines
Also I would add concurrency with ConcurrentHashMap. 

