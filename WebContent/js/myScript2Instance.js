let chunks = [];
let recorder = null;
let isRecording = false;

function record() {
    chunks.length = 0;
    let stream = document.querySelector('canvas').captureStream(30);
    recorder = new MediaRecorder(stream);
    recorder.ondataavailable = e => {
        if (e.data.size) {
            chunks.push(e.data);
        }
    };
    recorder.onstop = exportVideo;
    recorder.start();
}

function exportVideo(e) {
    let blob = new Blob(chunks);
    var vid = document.createElement('video');
    vid.id = 'recorded';
    vid.controls = true;
    vid.src = URL.createObjectURL(blob);
    document.body.appendChild(vid);
    vid.play();
}

function defineSketch(points) {
    return function (sketch) {
        const MAX_VELOCITY = 20;
        
        let time = 0;
        let x = [];
        let y = [];
        let fourierX, fourierY;
        let path = [];
        let coords;

        let maxX, maxY;

        let epiCSlider;
        let velSlider;

        sketch.setup = () => {
            sketch.createCanvas(650, 450);
            coords = points;

            for (let i = 0; i < coords.length; i++) {
                x.push(coords[i].x);
                y.push(coords[i].y);
            }

            maxX = Math.max.apply(null, x) * sketch.height / sketch.width;
            maxY = Math.max.apply(null, y) * sketch.height / sketch.width;

            fourierX = dft(x);
            fourierY = dft(y);

            fourierX.sort((a, b) => a.amp - b.amp);
            fourierY.sort((a, b) => a.amp - b.amp);

            epiCSlider = sketch.createSlider(0.01, 0.99, 0.99, 0.01);
            epiCSlider.id("epiCSlider");
            epiCSlider.addClass("slider custom-range col-8");
            epiCSlider.attribute("flength", fourierX.length);
            let divEpicS = sketch.createDiv('<label for="epiCSlider"># of Epicycles</label><br>');
            let epiValue = sketch.createSpan(Math.floor(epiCSlider.value() * fourierX.length));
            epiValue.addClass("valMonitor col mr-0");
            divEpicS.addClass("col-6");
            let divEpiSliderCtrl = sketch.createDiv();
            divEpiSliderCtrl.addClass("row mx-1");
            epiCSlider.parent(divEpiSliderCtrl);
            epiValue.parent(divEpiSliderCtrl);
            divEpiSliderCtrl.parent(divEpicS);

            velSlider = sketch.createSlider(0, MAX_VELOCITY - 1, MAX_VELOCITY - 1);
            velSlider.id("velSlider");
            velSlider.addClass("slider custom-range col-8");
            let divVel = sketch.createDiv('<label for="velSlider">Speed</label><br>')
            let velValue = sketch.createSpan(velSlider.value());
            velValue.addClass("valMonitor col mr-8");
            divVel.addClass("col-6");
            let divVelCtrl = sketch.createDiv();
            divVelCtrl.addClass("row m-1");
            velSlider.parent(divVelCtrl);
            velValue.parent(divVelCtrl);
            divVelCtrl.parent(divVel);

            let divCommands = sketch.createDiv();
            divCommands.addClass("row");
            divEpicS.parent(divCommands);
            divVel.parent(divCommands);

            if(isRecording) {
                record();
            }
        }

        function epiCycles(x, y, rotation, fourier) {
            let scaleParam = 0.7; // scaling image to fit canvas
            for (let i = Math.floor(fourier.length * (1 - epiCSlider.value())); i < fourier.length; i++) {
                let prevX = x;
                let prevY = y;
                let freq = fourier[i].freq;
                let radius = fourier[i].amp;
                let phase = fourier[i].phase;
                x += radius * sketch.cos(freq * time + phase + rotation) * scaleParam;
                y += radius * sketch.sin(freq * time + phase + rotation) * scaleParam;

                sketch.stroke(255, 100);
                sketch.noFill();
                sketch.ellipse(prevX, prevY, radius * 2);
                sketch.stroke(255);
                sketch.line(prevX, prevY, x, y);
            }

            return sketch.createVector(x, y);
        }

        sketch.draw = () => {
            sketch.background(0);

            let vx = epiCycles(sketch.width - (maxX + 20), sketch.floor(sketch.height / 10), 0, fourierX);
            let vy = epiCycles(sketch.floor(sketch.width / 10), sketch.height - (maxY + 20), sketch.HALF_PI, fourierY);
            let v = sketch.createVector(vx.x, vy.y);
            path.unshift(v);
            sketch.line(vx.x, vx.y, v.x, v.y);
            sketch.line(vy.x, vy.y, v.x, v.y);

            sketch.beginShape();
            sketch.noFill();
            for (let i = 0; i < path.length; i++) {
                sketch.vertex(path[i].x, path[i].y);
            }
            sketch.endShape();

            const dt = sketch.TWO_PI / (fourierY.length * (MAX_VELOCITY - velSlider.value()));
            time += dt;

            if (time > sketch.TWO_PI) {
                time = 0;
                path = [];

                if(isRecording) {
                    recorder.stop();
                    isRecording = false;
                }
            }

        }
    }
}