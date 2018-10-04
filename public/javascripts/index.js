$(function() {

    $("#getTweets").click(function () {
        if ($("#searchBar").val().length == 0) {
            alert("Please enter the search keyword");
            return;
        }
        else {
            $.get("/tellChildAboutnewQuery/"+$("#searchBar").val(),function (event) {
                console.log(event);
            });
        }
    });
    const ws = new WebSocket($("body").data("ws-url"));
    ws.onopen=function (ev) {
        console.log("Websocket opened");
    }
    ws.onmessage=function (ev) {
        /*var jsOb = JSON.parse(event.data);
        var screenName = jsOb.screenName;
        var name = jsOb.name;
        var tweet = jsOb.tweet;
        var htmlCode='<div class="card"><h3 class="card-header primary-color white-text" id="screenName">'+screenName+'</h3><div class="card-body"><h4 class="card-title" id="userName">'+name+'</h4><p class="card-text" id="tweet">'+tweet+'</p></div>';
        $("#contentDiv").append(htmlCode);*/
        var jsOb = JSON.parse(event.data);
        var screenName = jsOb.screenName;
        var name = jsOb.name;
        var tweet = jsOb.tweet;
        var htmlCode='<div class="card"><h3 class="card-header primary-color white-text" id="screenName">'+
            '<a id="linkToDisplay" href="displayTweets/'+screenName+'">'+screenName+'</a></h3><div class="card-body">' +
            '<h4 class="card-title" id="userName">'+name+'</h4>' +
            '<p class="card-text" id="tweet">'+tweet+'</p></div>';
        $("#contentDiv").append(htmlCode);
    }
    ws.onclose=function (ev) {
        console.log("Websockets closed");
    }
    ws.onerror=function(ev){
        console.log(ev);
        console.log("Error occured");
    }
});