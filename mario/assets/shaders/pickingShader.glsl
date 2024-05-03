#type vertex
#version 330 core
/*
layout is what coordinate with the glVertexAttribPointer() and glEnableVertexAttribArray().
glVertexAttribPinter() set up the attribution with a specific index, and glEnableVertexAttribArray() enable the given index.
Note that the location is corresponding to the index set above.
layout variables always change.
*/
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTextureCoords;
layout (location = 3) in float aTextureID;
layout (location = 4) in float aEntityID;
/*
uniform variable will stick around for object to object.
Always using and not mordified, store in a fast place to acess.
*/
uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTextureCoords;
out float fTextureID;
out float fEntityID;

void main() {
    fColor = aColor;
    fTextureCoords = aTextureCoords;
    fTextureID = aTextureID;
    fEntityID = aEntityID;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTextureCoords;
in float fTextureID;
in float fEntityID;

uniform sampler2D uTextures[8]; // The number of slots for textures. We can increase if performance allows.

out vec3 color;

void main() {
    // if fTextureID is 0, means no texture should be used.
    vec4 texColor = vec4(1,1,1,1);
    if (fTextureID > 0) {
        int id = int(fTextureID);
        texColor = fColor * texture(uTextures[id], fTextureCoords);
    }
    // Discard very low alpha value, like the center of donut, when picking, we discard the center of donut.
    if (texColor.a < 0.5) {
        discard;
    }
    color = vec3(fEntityID, fEntityID, fEntityID);
}