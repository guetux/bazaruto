$(function () {
  "use strict";
  
  setInterval(function () {
    $.get('/chat/messages/', function (data) {
      var messages = $('#messages');
      var chat = "";
      $.each(data, function(i, msg) {
         chat += msg.username + ": " + msg.message + "\n";
      });
      messages.text(chat);
      messages.scrollTop(messages[0].scrollHeight - messages.height());
    });
  }, 1000);
  
  $('#chatform').submit(function () {
      if ($('#message').val() === '') { return; }
      $.post('/chat/messages/', {
              'username': $('#username').val(),
              'message': $('#message').val()
      });
      $('#message').val('');
      return false;
  });
});