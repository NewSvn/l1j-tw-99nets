/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package l1j.server.server.datatables;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;
import static l1j.server.server.model.skill.L1SkillId.*;

public class CharBuffTable {
	private CharBuffTable() {
	}

	private static Logger _log = Logger
			.getLogger(CharBuffTable.class.getName());

	private static final int[] buffSkill = { 2, 67, // ライト、シェイプチェンジ
			3, 99, 151, 159, 168, // シールド、シャドウアーマー、アーススキン、アースブレス、アイアンスキン
			43, 54, 1000, 1001, STATUS_ELFBRAVE, // ヘイスト、グレーターヘイスト、ブレイブポーション、グリーンポーション、エルヴンワッフル
			52, 101, 150, // ホーリーウォーク、ムービングアクセレーション、ウィンドウォーク
			26, 42, 109, 110, // PE:DEX、PE:STR、ドレスマイティー、ドレスデクスタリティー
			114, 115, 117, // グローウィングオーラ、シャイニングオーラ、ブレイブオーラ
			148, 155, 163, // ファイアーウェポン、ファイアーブレス、バーニングウェポン
			149, 156, 166, // ウィンドショット、ストームアイ、ストームショット
			1002, 1005, // ブルーポーション、チャット禁止
			COOKING_1_0_N, COOKING_1_0_S, COOKING_1_1_N, COOKING_1_1_S, // 料理(デザートは除く)
			COOKING_1_2_N, COOKING_1_2_S, COOKING_1_3_N, COOKING_1_3_S,
			COOKING_1_4_N, COOKING_1_4_S, COOKING_1_5_N, COOKING_1_5_S,
			COOKING_1_6_N, COOKING_1_6_S, COOKING_2_0_N, COOKING_2_0_S,
			COOKING_2_1_N, COOKING_2_1_S, COOKING_2_2_N, COOKING_2_2_S,
			COOKING_2_3_N, COOKING_2_3_S, COOKING_2_4_N, COOKING_2_4_S,
			COOKING_2_5_N, COOKING_2_5_S, COOKING_2_6_N, COOKING_2_6_S,
			COOKING_3_0_N, COOKING_3_0_S, COOKING_3_1_N, COOKING_3_1_S,
			COOKING_3_2_N, COOKING_3_2_S, COOKING_3_3_N, COOKING_3_3_S,
			COOKING_3_4_N, COOKING_3_4_S, COOKING_3_5_N, COOKING_3_5_S,
			COOKING_3_6_N, COOKING_3_6_S };

	private static void StoreBuff(int objId, int skillId, int time, int polyId) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO character_buff SET char_obj_id=?, skill_id=?, remaining_time=?, poly_id=?");
			pstm.setInt(1, objId);
			pstm.setInt(2, skillId);
			pstm.setInt(3, time);
			pstm.setInt(4, polyId);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public static void DeleteBuff(L1PcInstance pc) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM character_buff WHERE char_obj_id=?");
			pstm.setInt(1, pc.getId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}

	public static void SaveBuff(L1PcInstance pc) {
		for (int skillId : buffSkill) {
			int timeSec = pc.getSkillEffectTimeSec(skillId);
			if (0 < timeSec) {
				int polyId = 0;
				if (skillId == SHAPE_CHANGE) {
					polyId = pc.getTempCharGfx();
				}
				StoreBuff(pc.getId(), skillId, timeSec, polyId);
			}
		}
	}

}