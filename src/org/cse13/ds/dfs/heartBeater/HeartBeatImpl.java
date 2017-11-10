package org.cse13.ds.dfs.heartBeater;

import org.cse13.ds.dfs.node.Node;

/**
 *
 * @author nadunindunil
 */
public class HeartBeatImpl implements HeartBeater {
    
    private volatile int hearBeat = 0;
    
    public HeartBeatImpl(){
    }
    
    public void upHeartBeat(){
        this.hearBeat = 0; 
    }
    
    public void downHeartBeat(){
        this.hearBeat--;
    }

    @Override
    public void handleHeartBeat() {
        
    }
    
}
