package slge;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.awt.event.KeyListener;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.*;

import slutils.*;

// Added by hand -shankar:
import static org.lwjgl.system.MemoryUtil.*;
import static slutils.slTime.getTime;

import slutils.*;


public class slWindow {

    private int width, height;
    private String title;
    private long glfwWindow;
    private static slScene currentScene = null;

    private static final int WINDOW_WIDTH  = 1920;
    private static final int WINDOW_HEIGHT = 1080;

    private static final float clear_color_red = 1.0f;
    private static final float clear_color_green = 1.0f;
    private static final float clear_color_blue = 1.0f;
    private static final float clear_color_alpha = 1.0f;

    public float red = clear_color_red;
    public float green = clear_color_green;
    public float blue = clear_color_blue;
    public float alpha = clear_color_alpha;
    private static slWindow my_window = null;
    private slWindow() {
        // Dimensions of the OpenGL window:
        this.width = WINDOW_WIDTH;
        this.height = WINDOW_HEIGHT;
        this.title = "CSCI 133";
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new slLevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene = new slLevelScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false : "Arrived at an unknown slScene: " + newScene;
                break;
        }  // switch (newScene)
    }

    public static slWindow get() {
        if (slWindow.my_window == null){
            slWindow.my_window = new slWindow();
        }
        return slWindow.my_window;
    }

    public static slScene getScene() {
        return get().currentScene;
    }

    public void run() {
        System.out.println("(c) California State University at Sacramento, Computer Science Department");
        System.out.println("It is illegal to host this code anywhere outside of csus.edu websites");
        System.out.print("EXCEPT FOR THE PURPOSE OF COURSE WORK AT CSUS BY THE STUDENTS REGISTERED AT THE UNIVERSITY\n");
        System.out.println("LWJGL Version: " + Version.getVersion());

        init();
        loop();

        // Clean up:
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Init GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Could not initialize GLFW");
        }

        // Configure GLFW:
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // Create window:
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("glfwCreateWindow(...) failed; bailing out!");
        }

        glfwSetCursorPosCallback(glfwWindow, slMouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, slMouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, slMouseListener::mouseScrollCallback);

        glfwSetKeyCallback(glfwWindow, slKeyListener::keyCallback);

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);

        glfwShowWindow(glfwWindow);

        GL.createCapabilities();

        slWindow.changeScene(0);
    }

    public void loop() {
        float beginTime = getTime();
        float endTime = getTime();
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();

            glClearColor(red, green, blue, alpha);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);

            endTime = getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

    }
}
