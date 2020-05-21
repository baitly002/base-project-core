package org.basecode.common.sequence.baidu;

import java.util.concurrent.TimeUnit;

public class SequenceUtil {

    public static SequenceModel parseId(Long uid){
        return new SequenceModel(uid);
    }

    public static Long getUid(Long time){
        long currentSecond = TimeUnit.MILLISECONDS.toSeconds(time);
        BitsAllocator bitsAllocator = new BitsAllocator(DefaultUidGenerator.timeBits, DefaultUidGenerator.workerBits, DefaultUidGenerator.seqBits);
        return bitsAllocator.allocate(currentSecond-DefaultUidGenerator.epochSeconds, 0, 0);
    }

//    public static void main(String[] args) {
//        Long uid = SequenceSingle.getIstance().nextId();
//        System.out.println(SequenceSingle.getIstance().parseId(uid));
//        System.out.println(JSON.toJSONString(parseId(uid)));
//        Date date = new Date();
//        System.out.println(getUid(date.getTime()));
//    }
}
