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

/*
uniform variable will stick around for object to object.
Always using and not mordified, store in a fast place to acess.
*/
uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;

void main() {
    fColor = aColor;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;

out vec4 color;

void main() {
    color = fColor;
}