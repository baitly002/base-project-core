package org.basecode.core.sequence.baidu;

import org.basecode.core.util.DateUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SequenceModel implements Serializable {

    private Long uid;
    private Long time;
    private String timeStr;
    private Long workerId;
    private Long sequence;

    public SequenceModel(Long uid){
        setUid(uid);
    }

    public Long getUid() {
        return uid;
    }

    public SequenceModel setUid(Long uid) {
        this.uid = uid;

        long totalBits = BitsAllocator.TOTAL_BITS;
        BitsAllocator bitsAllocator = new BitsAllocator(DefaultUidGenerator.timeBits, DefaultUidGenerator.workerBits, DefaultUidGenerator.seqBits);
        long signBits = bitsAllocator.getSignBits();
        long timestampBits = bitsAllocator.getTimestampBits();
        long workerIdBits = bitsAllocator.getWorkerIdBits();
        long sequenceBits = bitsAllocator.getSequenceBits();

        // parse UID
        long sequence = (uid << (totalBits - sequenceBits)) >>> (totalBits - sequenceBits);
        long workerId = (uid << (timestampBits + signBits)) >>> (totalBits - workerIdBits);
        long deltaSeconds = uid >>> (workerIdBits + sequenceBits);

        Date thatTime = new Date(TimeUnit.SECONDS.toMillis(DefaultUidGenerator.epochSeconds + deltaSeconds));
        String thatTimeStr = DateUtil.getFormatString(thatTime);

        String.format("{\"UID\":\"%d\",\"time\":\"%d\",\"timestamp\":\"%s\",\"workerId\":\"%d\",\"sequence\":\"%d\"}",
                uid, thatTime.getTime(), thatTimeStr, workerId, sequence);
        this.time = thatTime.getTime();
        this.timeStr = thatTimeStr;
        this.workerId = workerId;
        this.sequence = sequence;
        return this;
    }

    public Long getTime() {
        return time;
    }

    public SequenceModel setTime(Long time) {
        this.time = time;
        return this;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public SequenceModel setTimeStr(String timeStr) {
        this.timeStr = timeStr;
        return this;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public SequenceModel setWorkerId(Long workerId) {
        this.workerId = workerId;
        return this;
    }

    public Long getSequence() {
        return sequence;
    }

    public SequenceModel setSequence(Long sequence) {
        this.sequence = sequence;
        return this;
    }
}
