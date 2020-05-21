package org.basecode.core.sequence.baidu;

public interface WorkerIdAssigner {

    /**
     * Assign worker id for {@link com.baidu.fsg.uid.impl.DefaultUidGenerator}
     * 
     * @return assigned worker id
     */
    long assignWorkerId();

}
