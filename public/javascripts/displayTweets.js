$(document).ready(function(){
    $.get($("#apiAddress").val(),function(data){

        console.log(data);
        //console.log(data);
        //console.log(data.length);
        var screenName = data[0].screenName;
        var name = data[0].name;
        var friends = data[0].friendsCount;
        var loc = data[0].location;
        var followers = data[0].followersCount;
        var description = data[0].description;

        //console.log(screenName);

        var length=data.length;
        var userInfo="<h1 align='center'>"+name +"</h1>" +
            "<br><h5 align='center'>Followers count: "+followers+"</h5>"+
            "<h5 align='center'>Location: "+loc+"</h5>"+
            "<h5 align='center'>Description: "+ description+"</h5>"+
            "<h5 align='center'>Friends count: "+friends+"</h5>"+
            "<a href='https://twitter.com/"+screenName+"'>@"+ screenName+"</a>";
        $("#userInfo").html(userInfo);
        for(var i=0;i< length;i++){
            if(data[i]!=null){
                var tweet=data[i].tweet;
                $('<p>',{class:"list-group-item list-group-item-action waves-effect tweets", text:tweet }).appendTo("#contentDiv");
            }
        }

    });
    console.log("Non blocking confirmation");
});

