package com.example.karaoke_note.data


enum class GameKind(val displayName: String) {
    JOY_NATIONAL_SCORING_GP("[JOY] 全国採点GP"),
    JOY_ANALYSIS_SCORING_AI_PLUS("[JOY] 分析採点AI+"),
    JOY_ANALYSIS_SCORING_AI("[JOY] 分析採点AI"),
    JOY_ANALYSIS_SCORING_MASTER("[JOY] 分析採点マスター"),
    DAM_RANKING_BATTLE_ONLINE("[DAM] ランキングバトルONLINE"),
    DAM_PRECISE_SCORING_AI("[DAM] 精密採点Ai"),
    DAM_PRECISE_SCORING_DX_G("[DAM] 精密採点DX-G"),
    DAM_PRECISE_SCORING_DX_DUET("[DAM] 精密採点DXデュエット"),
    DAM_PRECISE_SCORING_DX("[DAM] 精密採点DX");

    companion object {
        fun fromDisplayName(displayName: String): GameKind? {
            return values().firstOrNull { it.displayName == displayName }
        }
    }
}