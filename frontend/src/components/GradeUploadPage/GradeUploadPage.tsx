import React from 'react';
import { Box } from '@mui/material';
import { GradeUpload } from '../GradeUpload/GradeUpload';
import TopBar from '../TopBar/TopBar';

const GradeUploadPage: React.FC = () => {
  return (
    <Box>
      <TopBar 
        title="ElectiveMatch - Grade Upload"
        showHomeButton={true}
      />
      
      <Box sx={{ p: 3 }}>
        <GradeUpload baseUrl="http://localhost:8080" />
      </Box>
    </Box>
  );
};

export default GradeUploadPage;
