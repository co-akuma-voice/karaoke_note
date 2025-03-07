package com.example.karaoke_note.data

enum class BrandKind(val displayName: String) {
    JOY("JOY"),
    DAM("DAM");

    companion object {
        fun fromDisplayName(displayName: String): BrandKind? {
            return values().firstOrNull { it.displayName == displayName }
        }
    }
}

enum class GameKind(val displayName: String) {
    JOY_NATIONAL_SCORING_GP("全国採点GP"),
    JOY_ANALYSIS_SCORING_AI_PLUS("分析採点AI+"),
    JOY_ANALYSIS_SCORING_AI("分析採点AI"),
    JOY_ANALYSIS_SCORING_MASTER("分析採点マスター"),
    DAM_RANKING_BATTLE_ONLINE("ランキングバトルONLINE"),
    DAM_PRECISE_SCORING_AI("精密採点Ai"),
    DAM_PRECISE_SCORING_DX_G("精密採点DX-G"),
    DAM_PRECISE_SCORING_DX_DUET("精密採点DXデュエット"),
    DAM_PRECISE_SCORING_DX("精密採点DX"),
    DAM_PRECISE_SCORING_AI_HEART("精密採点Ai Heart");

    companion object {
        fun fromDisplayName(displayName: String): GameKind? {
            return values().firstOrNull { it.displayName == displayName }
        }
        fun getBrandKind(gameKind: GameKind): BrandKind {
            return when (gameKind) {
                JOY_NATIONAL_SCORING_GP -> BrandKind.JOY
                JOY_ANALYSIS_SCORING_AI_PLUS -> BrandKind.JOY
                JOY_ANALYSIS_SCORING_AI -> BrandKind.JOY
                JOY_ANALYSIS_SCORING_MASTER -> BrandKind.JOY
                DAM_PRECISE_SCORING_AI_HEART -> BrandKind.DAM
                DAM_PRECISE_SCORING_AI -> BrandKind.DAM
                DAM_PRECISE_SCORING_DX_G -> BrandKind.DAM
                DAM_PRECISE_SCORING_DX_DUET -> BrandKind.DAM
                DAM_PRECISE_SCORING_DX -> BrandKind.DAM
                DAM_RANKING_BATTLE_ONLINE -> BrandKind.DAM
            }
        }
        fun getJoyGameKinds(): List<GameKind> {
            return values().filter { getBrandKind(it) == BrandKind.JOY }
        }
        fun getDamGameKinds(): List<GameKind> {
            return values().filter { getBrandKind(it) == BrandKind.DAM }
        }
    }
}