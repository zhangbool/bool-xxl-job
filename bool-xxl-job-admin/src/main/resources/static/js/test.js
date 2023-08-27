/*启动就调用*/
/*$(document).ready(function(){
    console.log("ivanl;;;;;")
})*/

/*等价上面*/
$(function () {
    console.log("启动初始化")

    $('#searchBtn').on('click', function (){
        console.log("searchBtn clicked");
    });


    var studentTable = $('#students').dataTable({
        "ajax": {
            url: base_url + "/getAllStudentsData" ,
            type: "post",
            "error": function (e) {
                console.log(e)
            },
            "dataSrc": function (d) {
                // 这里是获取结果数据
                console.log("=====获取到数据:" + JSON.stringify(d))
                // 拿到数据后是可以修改的
                // d.data = [{'no':10, 'name':'zhang', 'age':100}]
                return d.data;
            },
            data: function ( d ) {
                // 这个是发给服务器的参数.......
                // #todo: 这里d为啥拿不到数据呢, 怎么拿到呢
                // 这里拿的是配置数据吧, 因为我这里没加啥配置, 所以拿不到???吗
                console.log($('#zhang').text());

                console.log("请求到的students数据是: " + JSON.stringify(d));
                return null;
                // return d.data;
            }
        },
        "columns": [
            { "data": 'no', "visible" : true, "width":'20%'},
            { "data": 'name', "visible" : true, "width":'20%' },
            { "data": 'age', "visible" : true, "width":'20%'}
        ]

    })


});



/*加载完成调用*/
$(window).on('load', function(){
    console.log("页面加载完成")
});


$(window).on('unload', function () {
    console.log("页面离开页面")

});
