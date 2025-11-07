package com.example.Angler.Angler.dto;

/*
응답 DTO

{
  "score": 87,
  "message": "의심 문자로 보입니다",
  "action": "신고 권장"
}

신뢰도 (0~100)
결과 메시지 (예: “의심 문자로 보입니다”)
권장 조치 (예: "신고 권장”)

==========================================
응답DTO를 DB 저장 시 (DB 저장하는 엔티티로 Mapping 시)
로직에서 action을 Enum으로 빼서 DB에서 추후에 검색 시 Enum기준으로 필터링해서 수행가능하게
*/
public class AnglerResponseDto {

    private int reliability;
    private String resultMessage;
    private String action;

    public AnglerResponseDto(int reliability, String resultMessage, String action) {
        this.reliability = reliability;
        this.resultMessage = resultMessage;
        this.action = action;
    }

    public int getReliability() {
        return reliability;
    }

    public void setReliability(int reliability) {
        this.reliability = reliability;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
