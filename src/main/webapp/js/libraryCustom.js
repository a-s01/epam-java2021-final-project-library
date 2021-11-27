function addBook(id) {
        var id = id;
        $.ajax({
            url     : '/controller',
            method  : 'POST',
            data    : {id : id, command : 'booking.addBook'},
            success : function(resultText) {
                        $('#' + id).prop("disabled", true);
                        if ( !$( "#bookedBooksNum" ).length ) {
                            $("#bookedBooksNumParent").html('My booking<span class="position-absolute top-0 start-99 translate-middle badge rounded-pill bg-success" id="bookedBooksNum"></span>');
                        }
                        $( "#bookedBooksNum" ).text(resultText);
                        $('#addedBookAlert').show("slow", "swing", function(){ $('#addedBookAlert').delay(700).hide("slow") } );
                      },
            error   : function(jqXHR, exception) {
                        console.log('Error occured!!!');
                      },
        });
}

function check(el) {
        if (el.reportValidity()) {
            el.classList.remove("is-invalid");
            el.classList.add("is-valid");
        } else {
            el.classList.remove("is-valid");
            el.classList.add("is-invalid");
        }
}

function checkPass() {
      if (document.getElementById('password').value != null && document.getElementById('password').value ==
        document.getElementById('confirmPass').value) {
            document.getElementById('password').classList.remove("is-invalid");
            document.getElementById('confirmPass').classList.remove("is-invalid");
            document.getElementById('password').classList.add("is-valid");
            document.getElementById('confirmPass').classList.add("is-valid");
            document.getElementById('message').hidden = true;
      } else {
            document.getElementById('password').classList.add("is-invalid");
            document.getElementById('confirmPass').classList.add("is-invalid");
            document.getElementById('message').hidden = false;
      }
}

function makeValid(el) {
    el.classList.add("is-valid");
}