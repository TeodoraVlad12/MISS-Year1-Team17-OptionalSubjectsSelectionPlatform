import React, { useState, useCallback } from 'react';
import {
    Box,
    Paper,
    Typography,
    Button,
    Checkbox,
    FormControlLabel,
    Alert,
    CircularProgress,
    Divider,
    Chip,
    List,
    ListItem,
    ListItemText,
    ListItemIcon,
} from '@mui/material';
import {
    CloudUpload,
    CheckCircle,
    Error,
    InsertDriveFile,
    Assessment,
} from '@mui/icons-material';
import { GradeService, type GradeUploadResult } from '../../services/GradeService';

interface GradeUploadProps {
    baseUrl: string;
    onUploadComplete?: (result: GradeUploadResult) => void;
}

export const GradeUpload: React.FC<GradeUploadProps> = ({ baseUrl, onUploadComplete }) => {
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [overwrite, setOverwrite] = useState<boolean>(false);
    const [uploading, setUploading] = useState<boolean>(false);
    const [uploadResult, setUploadResult] = useState<GradeUploadResult | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [dragOver, setDragOver] = useState<boolean>(false);

    const gradeService = new GradeService(baseUrl);

    const handleFileSelect = useCallback((file: File) => {
        setError(null);
        setUploadResult(null);

        const validation = gradeService.validateCsvFile(file);
        if (!validation.isValid) {
            setError(validation.error || 'Invalid file');
            setSelectedFile(null);
            return;
        }

        setSelectedFile(file);
    }, [gradeService]);

    const handleFileInput = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (file) {
            handleFileSelect(file);
        }
    };

    const handleDrop = (event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        setDragOver(false);

        const files = event.dataTransfer.files;
        if (files.length > 0) {
            handleFileSelect(files[0]);
        }
    };

    const handleDragOver = (event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        setDragOver(true);
    };

    const handleDragLeave = () => {
        setDragOver(false);
    };

    const handleUpload = async () => {
        if (!selectedFile) return;

        setUploading(true);
        setError(null);
        setUploadResult(null);

        try {
            const result = await gradeService.uploadGrades(selectedFile, overwrite);
            setUploadResult(result);
            onUploadComplete?.(result);
        } catch (err) {
            setError(err instanceof Error ? err.message : String(err) || 'Upload failed');
        } finally {
            setUploading(false);
        }
    };

    const formatFileSize = (bytes: number): string => {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    };

    return (
        <Paper elevation={2} sx={{ p: 3, mb: 3 }}>
            <Box sx={{ mb: 3 }}>
                <Typography variant="h5" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Assessment color="primary" />
                    Upload Student Grades (CSV)
                </Typography>
                <Typography variant="body2" color="textSecondary">
                    Upload a CSV file containing student grades. Expected format: studentName,matricol,Course A,Course B,...
                </Typography>
            </Box>

            {/* File Drop Zone */}
            <Box
                onDrop={handleDrop}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                sx={{
                    border: 2,
                    borderStyle: 'dashed',
                    borderColor: dragOver ? 'primary.main' : 'grey.300',
                    borderRadius: 2,
                    p: 4,
                    textAlign: 'center',
                    cursor: 'pointer',
                    backgroundColor: dragOver ? 'action.hover' : 'background.paper',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                        borderColor: 'primary.main',
                        backgroundColor: 'action.hover',
                    },
                }}
                onClick={() => document.getElementById('file-input')?.click()}
            >
                <input
                    id="file-input"
                    type="file"
                    accept=".csv"
                    onChange={handleFileInput}
                    style={{ display: 'none' }}
                />
                
                <CloudUpload 
                    sx={{ 
                        fontSize: 48, 
                        color: dragOver ? 'primary.main' : 'grey.400',
                        mb: 2 
                    }} 
                />
                
                <Typography variant="h6" gutterBottom>
                    {dragOver ? 'Drop CSV file here' : 'Click to select or drag & drop CSV file'}
                </Typography>
                
                <Typography variant="body2" color="textSecondary">
                    Supported format: .csv (max 10MB)
                </Typography>
            </Box>

            {/* Selected File Info */}
            {selectedFile && (
                <Box sx={{ mt: 2 }}>
                    <Alert severity="success" icon={<InsertDriveFile />}>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <Box>
                                <Typography variant="subtitle2">{selectedFile.name}</Typography>
                                <Typography variant="caption" color="textSecondary">
                                    {formatFileSize(selectedFile.size)}
                                </Typography>
                            </Box>
                            <Chip label="Ready to upload" color="success" size="small" />
                        </Box>
                    </Alert>
                </Box>
            )}

            {/* Upload Options */}
            {selectedFile && (
                <Box sx={{ mt: 2 }}>
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={overwrite}
                                onChange={(e) => setOverwrite(e.target.checked)}
                                color="primary"
                            />
                        }
                        label={
                            <Box>
                                <Typography variant="body2">Overwrite existing grades</Typography>
                                <Typography variant="caption" color="textSecondary">
                                    Check this to update grades for students who already exist in the database
                                </Typography>
                            </Box>
                        }
                    />
                </Box>
            )}

            {/* Upload Button */}
            {selectedFile && (
                <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        size="large"
                        onClick={handleUpload}
                        disabled={uploading}
                        startIcon={uploading ? <CircularProgress size={20} /> : <CloudUpload />}
                        sx={{ minWidth: 200 }}
                    >
                        {uploading ? 'Uploading...' : 'Upload Grades'}
                    </Button>
                    
                    <Button
                        variant="outlined"
                        onClick={() => {
                            setSelectedFile(null);
                            setError(null);
                            setUploadResult(null);
                        }}
                        disabled={uploading}
                    >
                        Clear
                    </Button>
                </Box>
            )}

            {/* Error Display */}
            {error && (
                <Alert severity="error" sx={{ mt: 2 }} icon={<Error />}>
                    {error}
                </Alert>
            )}

            {/* Upload Results */}
            {uploadResult && (
                <Box sx={{ mt: 3 }}>
                    <Alert severity="success" icon={<CheckCircle />}>
                        <Typography variant="h6" gutterBottom>
                            Upload Completed Successfully!
                        </Typography>
                        <Typography variant="body2" sx={{ mb: 2 }}>
                            {uploadResult.summary}
                        </Typography>
                    </Alert>

                    <Paper elevation={1} sx={{ mt: 2, p: 2 }}>
                        <Typography variant="h6" gutterBottom>
                            Upload Statistics
                        </Typography>
                        <Divider sx={{ mb: 2 }} />
                        
                        <List dense>
                            <ListItem>
                                <ListItemIcon>
                                    <Chip label={uploadResult.totalRows} color="primary" size="small" />
                                </ListItemIcon>
                                <ListItemText 
                                    primary="Total Rows Processed" 
                                    secondary="Number of student records found in the CSV file" 
                                />
                            </ListItem>
                            
                            <ListItem>
                                <ListItemIcon>
                                    <Chip label={uploadResult.inserted} color="success" size="small" />
                                </ListItemIcon>
                                <ListItemText 
                                    primary="New Students Added" 
                                    secondary="Students that were newly created in the system" 
                                />
                            </ListItem>
                            
                            <ListItem>
                                <ListItemIcon>
                                    <Chip label={uploadResult.updated} color="warning" size="small" />
                                </ListItemIcon>
                                <ListItemText 
                                    primary="Existing Students Updated" 
                                    secondary="Students whose grades were updated" 
                                />
                            </ListItem>
                            
                            <ListItem>
                                <ListItemIcon>
                                    <Chip label={uploadResult.skipped} color="default" size="small" />
                                </ListItemIcon>
                                <ListItemText 
                                    primary="Students Skipped" 
                                    secondary="Students that already existed (overwrite was disabled)" 
                                />
                            </ListItem>
                        </List>
                    </Paper>
                </Box>
            )}

            {/* CSV Format Help */}
            <Box sx={{ mt: 3 }}>
                <Typography variant="body2" color="textSecondary" gutterBottom>
                    <strong>CSV Format Example:</strong>
                </Typography>
                <Paper variant="outlined" sx={{ p: 2, backgroundColor: 'grey.50' }}>
                    <Typography variant="caption" component="pre" sx={{ fontFamily: 'monospace' }}>
{`studentName,matricol,Mathematics,Physics,Chemistry
"John Doe",12345,9.5,8.0,7.5
"Jane Smith",67890,8.5,9.0,8.8`}
                    </Typography>
                </Paper>
                <Typography variant="caption" color="textSecondary" sx={{ mt: 1, display: 'block' }}>
                    • First column: Student name (can be quoted) <br />
                    • Second column: Matricol (student ID) <br />
                    • Remaining columns: Course grades (use course names as headers)
                </Typography>
            </Box>
        </Paper>
    );
};
