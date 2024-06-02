package SlRenderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class slShaderManager {

    enum ShaderType {
        VS, FS, TS, GS, HS, DS
    }
    private static final int MATRIX4x4_CAPACITY = 16;
    private static final int MATRIX3x3_CAPACITY = 9;
    private int spID; // 'ShaderProgramID'
    private boolean shaderInUse = false;
    private String vsSource;
    private String fsSource;
    private String str_filepath;
    private String[] rg_shaders;
    private int csProgram = 0;

    private final Pattern vs_pattern = Pattern.compile("#type\s*vertex");
    private final Pattern fs_pattern = Pattern.compile("#type\s*fragment");
    public slShaderManager(String str_my_filepath) {
        this.str_filepath = str_my_filepath;
        String shader_src = "";
        try {
            shader_src = new String(Files.readAllBytes(Paths.get(str_filepath)));
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error opening shader file: " + this.str_filepath;
        }

        // Take out any comment substrings at the beginning; strip the first "#type"
        // else String::split will inset a blank string at the first elements of the
        // split array:
        int my_index = shader_src.indexOf("#type") + "#type".length();
        shader_src = shader_src.substring(my_index).strip();
        rg_shaders  = shader_src.split("#type");

        for (int i = 0; i < rg_shaders.length; ++i){
            rg_shaders[i] = rg_shaders[i].strip();
            rg_shaders[i] = "#type " + rg_shaders[i];
        }  //  for (int i = 0; i < rg_shaders.length; ++i)
    }

    public int compile_shader(int indexToShaderArray) {

        Matcher vs_matcher = vs_pattern.matcher(rg_shaders[indexToShaderArray]);
        Matcher fs_matcher = fs_pattern.matcher(rg_shaders[indexToShaderArray]);
        int shaderID = -1;
        if (vs_matcher.find()) {
            shaderID = glCreateShader(GL_VERTEX_SHADER);
            // Need to remove "#type vertex\n" at the top - "\n*" and not "\n" - if we eliminate
            // the newline's elsewhere, we will end up with no new line after "\n"!
            String str_shader =
                    rg_shaders[indexToShaderArray].replaceAll("\s*#type\s*vertex\s*\n*", "");
            glShaderSource(shaderID, str_shader);
            glCompileShader(shaderID);
            glAttachShader(csProgram, shaderID);
            int retVal = glGetShaderi(shaderID, GL_COMPILE_STATUS);
            if (retVal == GL_FALSE) {
                int strLen = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH);
                System.out.println("VS from" + str_filepath + " failed to compile:");
                System.out.println(glGetShaderInfoLog(shaderID, strLen));
                assert false : "Vertex Shader compilation error";
            }
        } else if (fs_matcher.find()) {
            shaderID = glCreateShader(GL_FRAGMENT_SHADER);
            // Need to remove "#type fragment\n" at the top:
            String str_shader =
                    rg_shaders[indexToShaderArray].replaceAll("\s*#type\s*fragment\s*\n*", "");
            glShaderSource(shaderID, str_shader);
            glCompileShader(shaderID);
            glAttachShader(csProgram, shaderID);
            int retVal = glGetShaderi(shaderID, GL_COMPILE_STATUS);
            if (retVal == GL_FALSE) {
                int strLen = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH);
                System.out.println("ERROR: FS from " + str_filepath + " failed to compile:");
                System.out.println(glGetShaderInfoLog(shaderID, strLen));
                assert false : "Fragment Shader compilation error";
            }
        } else {
            System.out.println("Unknown shader received in the GLSL shader file");
            assert false : "Shader Compilation Error!";
        }
        return shaderID;
    }

    public void compose_shader_program(int vs_index, int fs_index){
        csProgram = glCreateProgram();
        int vs_id = compile_shader(vs_index);
        int fs_id = compile_shader(fs_index);
        glLinkProgram(csProgram);

        return ;
    }

    public int get_shader_program() {
        return this.csProgram;
    }

    public void set_shader_program() {
        if (!shaderInUse) {
            glUseProgram(csProgram);
            shaderInUse = true;
        }
    }

    public void detach_shader() {
        glUseProgram(0);
        shaderInUse = false;
    }

    public void loadMatrix4f(String strMatrixName, Matrix4f my_mat4) {
        int var_location = glGetUniformLocation(csProgram, strMatrixName);
        // this function is meaningless if the shader is not being used:
        set_shader_program();
        glUniformMatrix4fv(var_location, false, my_mat4.get(new float[MATRIX4x4_CAPACITY]));
    }

    // Since we are at it, we may as well add functions  to upload other data types:

    public void loadMatrix3f(String strMatrixName, Matrix3f my_mat3) {
        int var_location = glGetUniformLocation(csProgram, strMatrixName);
        // this function is meaningless if the shader is not being used:
        set_shader_program();
        glUniformMatrix3fv(var_location, false, my_mat3.get(new float[MATRIX3x3_CAPACITY]));
    }
    public void loadVec4f(String dataName, Vector4f my_v4){
        int var_location = glGetUniformLocation(csProgram, dataName);
        set_shader_program();
        glUniform4f(var_location, my_v4.x, my_v4.y, my_v4.z, my_v4.w );
    }

    public void loadVec3f(String dataName, Vector3f my_v3){
        int var_location = glGetUniformLocation(csProgram, dataName);
        set_shader_program();
        glUniform3f(var_location, my_v3.x, my_v3.y, my_v3.z );
    }

    public void loadVec2f(String dataName, Vector2f my_v2){
        int var_location = glGetUniformLocation(csProgram, dataName);
        set_shader_program();
        glUniform2f(var_location, my_v2.x, my_v2.y );
    }

    public void loadFloat(String dataName, float my_float){
        int var_location = glGetUniformLocation(csProgram, dataName);
        set_shader_program();
        glUniform1f(var_location, my_float);
    }

    public void loadInt(String dataName, int my_int){
        int var_location = glGetUniformLocation(csProgram, dataName);
        set_shader_program();
        glUniform1i(var_location, my_int);
    }

    public void loadTexture(String texName, int texSlot) {
        int texLocation = glGetUniformLocation(csProgram, texName);
        set_shader_program();
        glUniform1i(texLocation, texSlot);
    }

}
