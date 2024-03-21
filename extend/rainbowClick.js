/**
 * <h3>彩虹点击</h3>
 * @editor: daizc@bequick.run
 */
let rainbowClickConfig = {
    fontSize: 22,
    tag: ["富强", "民主", "文明", "和谐", "自由", "平等", "公正", "法治", "爱国", "敬业", "诚信", "友善"],
    color: [
        "#f62e74",
        "#f45330",
        "#ffc883",
        "#30d268",
        "#006cb4",
        "#784697",
        "#ffc883",
    ]
}

let a_idx = 0;
jQuery(document).ready(function ($) {
    $("body").click(function (e) {
        let $i = $("<span/>").text(rainbowClickConfig.tag[a_idx]);
        a_idx = (a_idx + 1) % rainbowClickConfig.tag.length;
        let x = e.pageX,
            y = e.pageY;
        $i.css({
            "z-index": 9999,
            "top": y - 13,
            "left": x - 20,
            "position": "absolute",
            "color": rainbowClickConfig.color[a_idx % rainbowClickConfig.color.length],
            "font-size": rainbowClickConfig.fontSizefontSize,
            "-moz-user-select": "none",
            "-webkit-user-select": "none",
            "-ms-user-select": "none"
        });
        $("body").append($i);
        $i.animate({
                "top": y - 180,
                "opacity": 0
            },
            1500,
            function () {
                $i.remove();
            });
    });
});
