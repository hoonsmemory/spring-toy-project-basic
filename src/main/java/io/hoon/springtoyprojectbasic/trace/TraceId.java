package io.hoon.springtoyprojectbasic.trace;

import java.util.UUID;

/**
 *  로그 추적을 하기 위한 클래스
 */
public class TraceId {

    private String id; // 트랜잭션 ID
    private int level; // 트랜잭션의 깊이

    public TraceId(){
        this.id = createId();
        this.level = 0;
    }

    private TraceId(String id, int level){
        this.id = id;
        this.level = level;
    }

    //각각의 HTTP 요청을 구분하기 위한 트랜잭션 ID값 생성
    private String createId(){
        return UUID.randomUUID().toString().substring(0, 8);
    }

    //실행중인 트랜잭션의 레벨을 증가하기 위한 코드
    public TraceId createNextId(){
        return new TraceId(id, level + 1);

    }

    //실행중인 트랜잭션의 레벨을 감소하기 위한 코드
    public TraceId createPreviousId(){
        return new TraceId(id, level - 1);

    }
    //첫번째 레벨인지 판단하기 위한 코드
    public boolean isFirstLever(){
        return level == 0;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }
}
