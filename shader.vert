#version 110

varying vec3 normal;

void main()
{
	gl_FrontColor = gl_Color;
	gl_TexCoord[0] = gl_MultiTexCoord0;
	normal = gl_NormalMatrix * gl_Normal;
	vec4 ver=gl_Vertex;

	ver.y *= 1.5;
	
	gl_Position = gl_ModelViewProjectionMatrix * ver;

}