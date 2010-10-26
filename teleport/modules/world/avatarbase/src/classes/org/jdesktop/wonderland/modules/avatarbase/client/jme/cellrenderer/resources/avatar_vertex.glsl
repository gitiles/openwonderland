/****************************************
* Autogenerated shader code follows
*     this is a placeholder header
*         resistance is futile
****************************************/

//////////// ATTRIBUTES ////////////
attribute vec4 boneIndices;

///////////// VARYING /////////////
varying vec3 ToLight;
varying vec3 VNormal;
varying vec3 ToCamera;

////////////  UNIFORMS ////////////
uniform mat4 pose[55];

///////////// GLOBALS /////////////
vec4 Position;
mat4 poseBlend;
mat3 TBNMatrix;

//////////// PROTOTYPES ///////////
void SimpleFTransform_Transfom();
void VertexDeformer_Transform();
void UnlitTexturing_Lighting();
void MeshColorModulation();
void CalculateToLight_Lighting();
void NormalMapping();
void SpecularMapping_Lighting();

/////////// MAIN LOGIC ////////////
void main(void)
{
	SimpleFTransform_Transfom();
        VertexDeformer_Transform();
        UnlitTexturing_Lighting();
	MeshColorModulation();
        CalculateToLight_Lighting();
        NormalMapping();
        SpecularMapping_Lighting();
	gl_Position = gl_ModelViewProjectionMatrix * Position;
}

/******************************************
* Function: SimpleFTransform_Transfom
*******************************************/
void SimpleFTransform_Transfom()
{
	Position = gl_Vertex;
}

/******************************************
* Function: VertexDeformer_Transform
*******************************************/
void VertexDeformer_Transform()
{
        vec3 weight = gl_Color.rgb;
	float weight4 = 1.0 - (weight.x + weight.y + weight.z);
	poseBlend = (pose[int(boneIndices.x)]) * weight.x +
	(pose[int(boneIndices.y)]) * weight.y +
	(pose[int(boneIndices.z)]) * weight.z +
	(pose[int(boneIndices.w)]) * weight4;
	VNormal.x = dot(gl_Normal, poseBlend[0].xyz);
	VNormal.y = dot(gl_Normal, poseBlend[1].xyz);
	VNormal.z = dot(gl_Normal, poseBlend[2].xyz);
	Position = Position * poseBlend;
}

/******************************************
* Function: UnlitTexturing_Lighting
*******************************************/
void UnlitTexturing_Lighting()
{
        gl_TexCoord[0] = gl_MultiTexCoord0;
}

/******************************************
* Function: MeshColorModulation
*******************************************/
void MeshColorModulation()
{
}

/******************************************
* Function: CalculateToLight_Lighting
*******************************************/
void CalculateToLight_Lighting()
{
        ToLight = normalize(vec3((gl_ModelViewMatrixInverse * gl_LightSource[0].position) - Position));
}

/******************************************
* Function: NormalMapping
*******************************************/
void NormalMapping()
{
        vec3 sccopy = gl_SecondaryColor.rgb;
        vec3 binormal = normalize(cross(sccopy, VNormal));
	TBNMatrix = mat3(gl_SecondaryColor.rgb, binormal, VNormal);
	ToLight *= TBNMatrix;
}

/******************************************
* Function: SpecularMapping_Lighting
*******************************************/
void SpecularMapping_Lighting()
{
ToCamera = (gl_ModelViewProjectionMatrix * vec4(0,0,1,1)).xyz;
	ToCamera *= TBNMatrix;
}

