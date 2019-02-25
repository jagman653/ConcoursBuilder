# ConcoursBuilder
Java Windows app for creating and managing JCNA Concours d'Elegance events. Creates judge assignments and conflict-free schedule. 
Materials for distribution to Entrants and Judges, such as windscreen placards and judge assignments, are also generated.

The user interface uses the Java Swing library to create dialogs. All information entered by the user is maintained in a SQLite 
database to avoid reentry in subsequent events.

At the core of ConcoursBuilder are graph-theoretic algorithms. First, for an algorith for matching on bipartite graphs is used to assign
judges to Jaguar classes, and then a 2-coloring algorithm is used to create a conflict-free schedule. The latter is based on C++ code
developed by collaborators at PNNL. Fundamental graph algorithms from Algoritms, 4th ed, by Sedgewick & Wayne, as implemented in algs4 
from Princton University.

While ConcourseBuilder itself applies only to concourses following rules prescribed by the Jaguar Clubs of North America (JCNA),
the fundamental algorithms and much of the code could be more broadly applied.
