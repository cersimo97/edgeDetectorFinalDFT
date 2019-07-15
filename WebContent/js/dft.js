function dft(x) {
    let fourier = [];
    let N = x.length;
    for (let k = 0; k < N; k++) {
        let re = 0;
        let im = 0;
        for (let n = 0; n < N; n++) {
            const theta = (2 * Math.PI * k * n) / N;
            re += x[n] * Math.cos(theta);
            im -= x[n] * Math.sin(theta);

        }
        re /= N;
        im /= N;

        let amp = Math.sqrt(re * re + im * im);
        let phi = Math.atan2(im, re);
        let X = {
            re: re,
            im: im,
            amp: amp,
            freq: k,
            phase: phi
        };

        fourier.push(X);
    }
    console.log("Fourier length (before reduction)", fourier.length);
    fourier = fourier.slice(0, Math.floor(fourier.length * 0.4));
    console.log("Fourier length (after reduction)", fourier.length);
    return fourier;
}