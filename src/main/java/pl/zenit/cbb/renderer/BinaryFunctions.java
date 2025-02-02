package pl.zenit.cbb.renderer;

import java.util.HashMap;
import java.util.function.Supplier;

public class BinaryFunctions extends HashMap<String, Equation> {

    private final Supplier<Double> param;

    public BinaryFunctions(Supplier<Double> param) {
        this.param = param;
        put("deformedbrot", c -> wrap(c, this::deformedbrotIteration));
        put("deformedbrot2", c -> wrap(c, this::deformedbrot2Iteration));
        put("deformedbrot3", c -> wrap(c, this::deformedbrot3Iteration));

        put("ABSCH 1", c -> wrap(c, this::absCh1Iteration));
        put("ABSCH 2", c -> wrap(c, this::absCh2Iteration));
        put("ABSCH 3", c -> wrap(c, this::absCh3Iteration));
        put("CH/M", c -> wrap(c, this::ch_m));
    }

    private Coords wrap(Coords input, Equation eq) {
        Coords reduced = eq.apply(input);
        return new Coords(reduced.x, reduced.y);
    }

    private Coords square(Coords input) {
        return new Coords(
                (param.get() - input.x) / param.get(),
                (param.get() - input.y) / param.get());

    }

    private Coords deformedbrotIteration(Coords input) {
        final double scale = 100;
        double real = input.x / scale;
        double imag = input.y / scale;
        double nextZReal = real;
        double nextZImag = imag;
        nextZReal = real * real - imag * imag + real;
        nextZImag = param.get() * real * imag + imag;
        return new Coords(nextZReal * scale, nextZImag * scale);
    }

    private Coords deformedbrot2Iteration(Coords input) {
        final double scale = 100;
        double real = input.x / scale;
        double imag = input.y / scale;
        double nextZReal = real;
        double nextZImag = imag;
        nextZReal = real * real - imag * imag - real;
        nextZImag = param.get() * real * imag + imag;
        return new Coords(nextZReal * scale, nextZImag * scale);
    }

    private Coords deformedbrot3Iteration(Coords input) {
        final double scale = 100;
        double real = input.x / scale;
        double imag = input.y / scale;
        double nextZReal = real;
        double nextZImag = imag;
        nextZReal = Math.pow(param.get(), real) - imag * imag + real;
        nextZImag = real * real * imag + imag;
        return new Coords(nextZReal * scale, nextZImag * scale);
    }

    private Coords absCh1Iteration(Coords input) {
        final double scale = 100;
        double real = input.x / scale;
        double imag = input.y / scale;
        double nextZReal = real;
        double nextZImag = imag;
        nextZReal = real * real - imag * imag + real;
        nextZReal = Math.abs(real * real - (imag * imag - real));
        nextZImag = param.get() * Math.abs(real) * Math.abs(1d - imag) + imag;
        return new Coords(nextZReal * scale, nextZImag * scale);
    }

    private Coords absCh2Iteration(Coords input) {
        final double scale = 100;
        double real = input.x / scale;
        double imag = input.y / scale;
        double nextZReal = real;
        double nextZImag = imag;
        nextZReal = real * real - imag * imag + real;
        nextZReal = Math.abs(real * real - (imag * imag - real));
        nextZImag = param.get() * Math.abs(1d - real) * Math.abs(1d - imag) + imag;
        return new Coords(nextZReal * scale, nextZImag * scale);
    }

    private Coords absCh3Iteration(Coords input) {
        final double scale = 100;
        double real = input.x / scale;
        double imag = input.y / scale;
        double nextZReal = real;
        double nextZImag = imag;
        nextZReal = Math.abs(real * real - imag * imag) + real;
        nextZImag = param.get() * real * imag + imag;
        return new Coords(nextZReal * scale, nextZImag * scale);
    }

    private Coords ch_m(Coords input) {
        final double scale = 100;
        double real = input.x / scale;
        double imag = input.y / scale;
        double nextZReal = real;
        double nextZImag = imag;
        nextZReal = param.get() * (1d - imag);
        nextZImag = real * imag + imag * imag;

        return new Coords(nextZReal * scale, nextZImag * scale);
    }

}