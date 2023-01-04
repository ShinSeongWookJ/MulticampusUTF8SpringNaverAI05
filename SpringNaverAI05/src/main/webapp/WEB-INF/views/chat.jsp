<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>:::chat:::</title>
<script
	src="https://cdn.jsdelivr.net/npm/jquery@3.6.1/dist/jquery.min.js"></script>
<script>
	let username='Tom';
	$(function(){
		const websocket = new WebSocket("ws://localhost:9090/app/ws/chat");
		alert(websocket);
        websocket.onmessage = onMessage;
        websocket.onopen = onOpen;
        websocket.onclose = onClose;
	})
	
	//채팅창에서 나갔을 때
            function onClose(evt) {
                var str = username + ": 님이 방을 나가셨습니다.";
                websocket.send(str);
            }
            
            //채팅창에 들어왔을 때
            function onOpen(evt) {
                var str = username + ": 님이 입장하셨습니다.";
                websocket.send(str);
            }

            function onMessage(msg) {
            	var data = msg.data;
                var sessionId = null;
                //데이터를 보낸 사람
                var message = null;
                var arr = data.split(":");

                for(var i=0; i<arr.length; i++){
                    console.log('arr[' + i + ']: ' + arr[i]);
                }
            }
</script>
</head>
<body>

</body>
</html>