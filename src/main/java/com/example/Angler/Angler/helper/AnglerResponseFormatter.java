package com.example.Angler.Angler.helper;

import com.example.Angler.Angler.dto.AnglerResponseDto;

public class AnglerResponseFormatter {
    public static String format(AnglerResponseDto dto) {
        if (dto == null) {
            return "피싱 문자가 아니에요.";
        }

        StringBuilder sb = new StringBuilder();
        int score = (int) Math.round(dto.getTrustScore());
        
        sb.append(getMessage(score, dto.isPhishing()));

        // 자세한 정보 필요시 주석 풀기
        /*
        if (dto.getReasons() != null && !dto.getReasons().isEmpty()) {
            sb.append("\n\n판정 근거:\n");
            dto.getReasons().forEach(reason -> sb.append("• ").append(reason).append("\n"));
        }
        */
        return sb.toString();
    }
    
    private static String getMessage(int score, boolean isPhishing) {
        if (isPhishing) {
            return String.format("🚨 위험해요 (%d점)\n피싱 문자예요. 절대 클릭하거나 송금하지 마세요.", score);
        } else if (score >= 80) {
            return String.format("✅ 안전해요 (%d점)\n위험 요소가 거의 없어요.", score);
        } else if (score >= 50) {
            return String.format("⚠️ 주의하세요 (%d점)\n위험도가 낮지만, 한 번 더 확인해보세요.", score);
        } else if (score >= 30) {
            return String.format("⚠️ 조심하세요 (%d점)\n의심스러운 요소가 있어요. 클릭을 권장하지 않아요.", score);
        } else {
            return String.format("🚨 매우 위험해요 (%d점)\n피싱 가능성이 높아요. 절대 클릭하지 마세요.", score);
        }
    }


}