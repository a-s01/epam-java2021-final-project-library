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

function findAuthor() {
    var query = $("#authorQuery").val();
    $('#searchResults').empty();
    $.ajax({
                url     : '/controller',
                method  : 'GET',
                data    : {query: query, command: 'author.findAll', searchBy: 'name'},
                dataType: "xml",
                success : function(xml) {
                            var error = $(xml).find('error').text();
                            if (error) {
                                var li = document.createElement('li');
                                $(li).html(`<a class="dropdown-item" onclick='this.classList.remove("show");'>${error}</a></li>`);
                                $('#searchResults').append(li);
                                $('#searchResults').addClass("show");
                            } else {
                                $(xml).find('author').each(
                                    function() {
                                        var authorID = $(this).find('id').text();
                                        var authorName = $(this).find('name').text();
                                        var li = document.createElement('li');
                                        $(li).html(`<a class="dropdown-item" onclick="chooseAuthor(${authorID},'${authorName}');">${authorName}</a></li>`);
                                        $('#searchResults').append(li);
                                        $('#searchResults').addClass("show");
                                    }
                                );
                            }
                        },
                error   : function(jqXHR, exception) {
                            console.log('Error occured!!!');
                          },
    });
}

function chooseAuthor(authorID, authorName) {
    if (document.getElementById("selectedAuthors" + authorID) == null) {
        var newSelected = document.createElement("div");
        newSelected.classList.add("form-check");
        newSelected.innerHTML = '<input type="checkbox" name="authorIDs" class="form-check-input" id="selectedAuthors'+ authorID + '"' +
                                    "value='" + authorID + "' checked/>" +
                                        '<label class="form-check-label" for="selectedAuthors'+ authorID + '">' +
                                          authorName +
                                        '</label>';

        document.getElementById('selectedAuthors').appendChild(newSelected);
        document.getElementById('searchResults').classList.remove("show");
    }
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