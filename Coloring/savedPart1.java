                ////////////////////////////   PART 2  /////////////////////////////////////////////
                //Detect Conflicts:
                for (int qi=0; qi<qConflict1.size(); qi++) {
                    v = qConflict1.peek();
                    //Traverse the adjacency set of vertex v, and it's adjacencies
                    for(Integer k  : G.adj(v)){
                        for(Integer w : G.adj(k)){
                          //int w = kk;
                          if ( (w == v) || (vertexColors[v] == -1) ) //Link back/preprocess
                            continue;
                          if ( vertexColors[v] == vertexColors[w] ) {
                            //Q.push_back(v or w) 
                            if ( (theRandomNumbers[v] < theRandomNumbers[w]) ||  ((theRandomNumbers[v] == theRandomNumbers[w])&&(v < w)) ) {
                              //int whereInQ = __sync_fetch_and_add(&QtmpTail, 1);
                              //int whereInQ = QtmpTail++;
                              //Qtmp[whereInQ] = v;//Add to the queue
                              qConflict2.enqueue(v);
                              vertexColors[v] = -1;  //Will prevent v from being in conflict in another pairing
                              break;
                            } //If rand values			
                        } //End of if( vtxColor[v] == vtxColor[verInd[k]] )
                      } // end inner adj traversal
                    } // end outer adj traversal
               } //End of outer for loop on Qi
                nLoops++;
                // Clear qConflict1
                while(!qConflict1.isEmpty()){
                    qConflict1.dequeue();
                }
                //Copy qConflict2 to qConflict1
                while(!qConflict2.isEmpty()){
                    qConflict1.enqueue(qConflict2.dequeue());
                } 
