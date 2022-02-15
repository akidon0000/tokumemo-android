package com.tokudai0000.tokumemo

enum class MenuLists {
    courseManagementHomePC,
    courseManagementHomeMobile,
    manabaHomePC,
    manabaHomeMobile,
    libraryWebHomePC,               // 図書館Webサイト常三島
    libraryWebHomeKuraPC,           // 図書館Webサイト蔵本
    libraryWebHomeMobile,
    libraryMyPage,                  // 図書館MyPage
    libraryBookLendingExtension,    // 図書館本貸出し期間延長
    libraryBookPurchaseRequest,     // 図書館本購入リクエスト
    libraryCalendar,                // 図書館カレンダー
    syllabus,                       // シラバス
    timeTable,                      // 時間割
    currentTermPerformance,         // 今年の成績表
    termPerformance,                // 成績参照
    presenceAbsenceRecord,          // 出欠記録
    classQuestionnaire,             // 授業アンケート
    mailService,                    // メール
    tokudaiCareerCenter,            // キャリアセンター
    tokudaiCoop,                    // 徳島大学生活共同組合
    courseRegistration,             // 履修登録
    systemServiceList,              // システムサービス一覧
    eLearningList,                  // Eラーニング一覧
    universityWeb,                  // 大学サイト

    customize,                       // 並び替え
    firstViewSetting,               // 初期画面設定
    password,                       // パスワード
    aboutThisApp,                   // このアプリについて
}

class Menu ( var title: String,             // 表示名ユーザーが変更することができる
             val id: MenuLists,
             val url: String )
//             var isDisplay: Boolean = true,    // 初期値は全てfalse(ユーザー「常三島生、蔵本生」によって表示内容を変更させる)
//             var isInitView: Boolean = false,  // 初期値は「マナバPC版」のみtrue
//             val canInitView: Boolean )       // 初期画面として設定可能か

class Constant {

    companion object {
        //ユーザーリストでデーターを追加
        val menuLists = arrayListOf<Menu> (
                Menu( "教務事務システム", MenuLists.courseManagementHomeMobile, ""),
                Menu( "マナバ", MenuLists.manabaHomePC, ""),
                Menu( "図書貸し出し延長", MenuLists.libraryBookLendingExtension,""),
                Menu( "開館カレンダー", MenuLists.libraryCalendar,""),
                Menu( "生協営業状況", MenuLists.tokudaiCoop,""),
                Menu( "シラバス", MenuLists.syllabus,""),
                Menu( "今期の成績", MenuLists.currentTermPerformance,""),
                Menu( "メール", MenuLists.mailService,""),
                Menu( "パスワード", MenuLists.password,""),
                Menu( "カスタマイズ", MenuLists.customize,""),
                Menu( "初期画面設定", MenuLists.firstViewSetting,""),
                Menu( "このアプリについて", MenuLists.aboutThisApp,"")
        )
    }

}