$(document).ready(function () {
    bsCustomFileInput.init();

    $("#myForm").submit(function (event) {
        event.preventDefault();
        $("#btnUpload span").toggleClass("d-none");
        if(!$("#controlsDescr").hasClass("d-none")) {
            $("#controlsDescr").addClass("d-none");
        }
        let fd = new FormData(document.getElementById("myForm"));
        $.ajax({
            type: "post",
            url: "http://localhost:8080/edgeDetectorFinal/EdgeDetectorServlet",  
            data: fd,
            processData: false,
            contentType: false
        }).done(function (response) {
            // console.log("OK - Received!");
            $("#btnUpload span").toggleClass("d-none");
            $("#controlsDescr").removeClass("d-none");
            startSketch(response);
        }).fail(function (response) {
            $("#btnUpload span").toggleClass("d-none");
            if(!$("#controlsDescr").hasClass("d-none")) {
                $("#controlsDescr").addClass("d-none");
            }
            showError(response.responseJSON);
        });
    });

    $('#sketch-holder').on('change', '#epiCSlider', () => {
        let slider = $('#epiCSlider');
        $(slider.next('.valMonitor')).text(Math.floor(slider.val() * slider.attr("flength")) + 1);
    });

    $('#sketch-holder').on('change', '#velSlider', () => {
        let slider = $('#velSlider');
        $(slider.next('.valMonitor')).text(parseInt(slider.val()) + 1);
    });
});

function startSketch(response) {
    let coordPoints = response.message;
    if (coordPoints) {
        console.log(coordPoints.length);
        $("#sketch-holder").empty();
        let mySketch = defineSketch(coordPoints);
        let canvasSketch = new p5(mySketch, 'sketch-holder');
    }
}

function showError(response) {
    let mess = response.message;
    $("#sketch-holder").empty()
        .html('<p class="lead text-danger">An error occurred :(<br>' + mess + '</p>');
}