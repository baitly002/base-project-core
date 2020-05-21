package org.basecode.common.sequence;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

public class MybatisSequenceGenerator implements IdentifierGenerator {

    public Long nextId(Object entity){
        return SequenceSingle.getIstance().nextId();
    }
}
