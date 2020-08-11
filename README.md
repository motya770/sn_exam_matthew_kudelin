Input and output files are located in /src/main/resources/
You can run this project as java main file ServiceNowExamApplication.java in IDE. (Based on Spring Boot). 

This code complexity is  O(n + n^2) because I read all lines O(n) and also use foreach inside foreach - which leads to O(n^2). 
This algorithm doesn’t scale well, because of the complexity and because of the reading all lines at once to the memory.

If I had more time I would implement an algorithm based on HashMap, when reading lines in the first run I would create HashMap where the key is the Line object that contains 
Sentence and can compare it to the next sentence and if not similarity is found then it added as a new key/value to the map but if found current line is added to the value (and the value is LineGroup - an object that stores similar lines). In the end, you can iterate over the map and print all LineGroups where value is bigger than one line. This will require O(n) + O(1).
Also, I would add concurrency with ConcurrentHashMap.

Also, maybe on the graph where each word (except dates) in the sentence is stored inside the leaf and this undirected graph creates all possible sentences. Then I will need to find all paths where there is the only difference in one node (word) and the length of the path is the same. This can allow traveling on the tree based on the complexity of N Log N.


