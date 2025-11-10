package com.example.Angler.Angler.helper;

import com.example.Angler.Angler.dto.AnglerResponseDto;

public class AnglerResponseFormatter {
    public static String format(AnglerResponseDto dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("이 문자는 ")
                .append(Math.round(dto.getTrustScore()))
                .append("점의 위험도가 있습니다.\n");

        sb.append(getMessage(dto.getTrustScore(), dto.isPhishing())).append("\n");

        // 자세한 정보 필요시 주석 풀기
        /*
        if (dto.getReasons() != null && !dto.getReasons().isEmpty()) {
            sb.append("판정 근거:\n");
            dto.getReasons().forEach(reason -> sb.append("- ").append(reason).append("\n"));
        }
        */
        return sb.toString();
    }

    private static String getMessage(double trustScore, boolean isPhishing) {
        if (trustScore >= 75 && isPhishing) {
            return "의심 링크이니 클릭하지 마세요.";
        } else if (trustScore >= 40) {
            return "위험도가 중간 수준이므로 주의하세요.(권장하지 않음)";
        } else if (trustScore >= 20) {
            return "위험도가 낮으나, 주의해서 확인하세요.";
        } else {
            return "위험 요소가 거의 없으므로 안전합니다.";
        }
    }
}