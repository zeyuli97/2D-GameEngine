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

/*
uniform variable will stick around for object to object.
Always using and not mordified, store in a fast place to acess.
*/
uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTextureCoords;
out float fTextureID;

void main() {
    fColor = aColor;
    fTextureCoords = aTextureCoords;
    fTextureID = aTextureID;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTextureCoords;
in float fTextureID;

uniform sampler2D uTextures[8]; // The number of slots for textures. We can increase if performance allows.

out vec4 color;

void main() {
    // if fTextureID is 0, means no texture should be used.
    if (fTextureID > 0) {
        int id = int(fTextureID);
        color = fColor * texture(uTextures[id], fTextureCoords);
    } else {
        color = fColor;
    }
}