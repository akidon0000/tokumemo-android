package com.tokudai0000.tokumemo.ui.menu

import androidx.lifecycle.ViewModel
import com.tokudai0000.tokumemo.Constant
import com.tokudai0000.tokumemo.Menu
import java.util.*

class MenuModel: ViewModel() {
    // TableCellの内容
    public var menuLists = arrayListOf<Menu>()

    /// 大学図書館の種類
//    enum LibraryType {
//        case main // 常三島本館
//        case kura // 蔵本分館
//    }
    /// 図書館の開館カレンダーPDFまでのURLRequestを作成する
    ///
    /// PDFへのURLは状況により変化する為、図書館ホームページからスクレイピングを行う
    /// 例1：https://www.lib.tokushima-u.ac.jp/pub/pdf/calender/calender_main_2021.pdf
    /// 例2：https://www.lib.tokushima-u.ac.jp/pub/pdf/calender/calender_main_2021_syuusei1.pdf
    /// ==HTML==[常三島(本館Main) , 蔵本(分館Kura)でも同様]
    /// <body class="index">
    ///   <ul>
    ///       <li class="pos_r">
    ///         <a href="pub/pdf/calender/calender_main_2021.pdf title="開館カレンダー">
    ///   ========
    ///   aタグのhref属性を抽出、"pub/pdf/calender/"と一致していれば、例1のURLを作成する。
    /// - Parameter type: 常三島(本館Main) , 蔵本(分館Kura)のどちらの開館カレンダーを欲しいのかLibraryTypeから選択
    /// - Returns: 図書館の開館カレンダーPDFまでのURLRequest
//    public func makeLibraryCalendarUrl(type: LibraryType) -> URLRequest? {
//        var urlString = ""
//        switch type {
//            case .main:
//            urlString = Url.libraryHomePageMainPC.string()
//            case .kura:
//            urlString = Url.libraryHomePageKuraPC.string()
//        }
//        let url = URL(string: urlString)! // fatalError
//        do {
//            // URL先WebページのHTMLデータを取得
//            let data = NSData(contentsOf: url as URL)! as Data
//            let doc = try HTML(html: data, encoding: String.Encoding.utf8)
//                // aタグ(HTMLでのリンクの出発点と到達点を指定するタグ)を抽出
//                for node in doc.xpath("//a") {
//                    // href属性(HTMLでの目当ての資源の所在を指し示す属性)に設定されている文字列を出力
//                    guard let str = node["href"] else {
//                    AKLog(level: .ERROR, message: "[href属性出力エラー]: href属性に設定されている文字列を出力する際のエラー")
//                    return nil
//                }
//                    // 開館カレンダーは図書ホームページのカレンダーボタンにPDFへのURLが埋め込まれている
//                    if str.contains("pub/pdf/calender/") {
//                        // PDFまでのURLを作成する(本館のURLに付け加える)
//                        let pdfUrlString = Url.libraryHomePageMainPC.string() + str
//
//                        if let url = URL(string: pdfUrlString) {
//                        return URLRequest(url: url)
//                    } else {
//                        AKLog(level: .ERROR, message: "[URLフォーマットエラー]: 図書館開館カレンダーURL取得エラー \n pdfUrlString:\(pdfUrlString)")
//                        return nil
//                    }
//                    }
//                }
//                AKLog(level: .ERROR, message: "[URL抽出エラー]: 図書館開館カレンダーURLの抽出エラー \n urlString:\(url.absoluteString)")
//            } catch {
//                AKLog(level: .ERROR, message: "[Data取得エラー]: 図書館開館カレンダーHTMLデータパースエラー\n urlString:\(url.absoluteString)")
//            }
//            return nil
//        }

    /// 今年度の成績表のURLを作成する
    ///
    /// - Note:
    ///   2020年4月〜2021年3月までの成績は https ... Results_Get_YearTerm.aspx?year=2020
    ///   2021年4月〜2022年3月までの成績は https ... Results_Get_YearTerm.aspx?year=2021
    ///   なので、2022年の1月から3月まではURLがyear=2021とする必要あり
    /// - Returns: 今年度の成績表のURL
    public fun createCurrentTermPerformanceUrl(): String {
        // 2020年4月〜2021年3月までの成績は https ... Results_Get_YearTerm.aspx?year=2020
        // 2021年4月〜2022年3月までの成績は https ... Results_Get_YearTerm.aspx?year=2021

        // 現在時刻の取得
        // Dateを作成すると現在日時が入るし、CalenderをgetInstanceでも現在日時が入る
        val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN);
        var year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH) + 1 // 1月が0、12月が11であることに注意

        // 1月から3月までは前年の成績
        if (month <= 3) {
            year -= 1
        }
        return "https://eweb.stud.tokushima-u.ac.jp/Portal/StudentApp/Sp/ReferResults/SubDetail/Results_Get_YearTerm.aspx?year=" + "${year.toString()}"
    }

    /// メニューリストにある特定のセルのインデックスを探す
    /// - Parameter id: インデックスを特定したいセルのID
    /// - Returns: インデックス番号
//    public fun searchIndexCell(id: Constant.MenuLists): Int? {
//        for (i in 0..<menuLists.count) {
//            if (menuLists[i].id == id) {
//                return i
//            }
//        }
//        for (i in 0..<Constant.initSettingLists.count) {
//            if (Constant.initSettingLists[i].id == id) {
//                return i
//            }
//        }
//        return nil
//    }
}